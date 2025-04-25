package will.dev.BTBTEST.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import will.dev.BTBTEST.entity.Jwt;
import will.dev.BTBTEST.entity.RefreshToken;
import will.dev.BTBTEST.entity.Role;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.enums.TypeDeRole;
import will.dev.BTBTEST.repository.JwtRepository;
import will.dev.BTBTEST.repository.UserRepository;
import will.dev.BTBTEST.services.UserService;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.*;

import static will.dev.BTBTEST.security.KeyGeneratorUtil.generateEncryptionKey;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtRepository jwtRepository;
    public static final String BEARER = "Bearer";
    public static final String REFRESH = "refresh";
    private final String ENCRYPTION_KEY = generateEncryptionKey(32);

    public Map<String, String> generate(String username){
        User user = (User) this.userService.loadUserByUsername(username);
        this.disableTokens(user);//---------------------Désactivation de tous les tokens lié a l'utilisateur précedement creer
        Map<String, String> jwtMap = new java.util.HashMap<>(this.generateJwt(user));
        //-----------------Branch refresh-token
        RefreshToken refreshToken = RefreshToken
                .builder()
                .valeur(UUID.randomUUID().toString())
                .expire(false)
                .creation(Instant.now())
                .expiration(Instant.now().plusMillis(30*60*1000))
                .build();
        // début branch déconnexion
        Jwt jwt = Jwt
                .builder()
                .valeur(jwtMap.get(BEARER))
                .desactive(false)
                .expire(false)
                .user(user)
                .refreshToken(refreshToken)//-----------------Branch refresh-token
                .build();
        this.jwtRepository.save(jwt);//Sauvegade la valeur du token dans la bd
        //Fin déconnexion
        jwtMap.put(REFRESH, refreshToken.getValeur());
        return jwtMap;
    }

    private void disableTokens(User user){
        final List<Jwt> jwtList = this.jwtRepository.findUser(user.getEmail())
                .filter(jwt -> jwt.getUser() != null) // Évite les valeurs null
                .peek(jwt ->{
                            jwt.setDesactive(true);
                            jwt.setExpire(true);
                        }
                ).collect(Collectors.toList());
        this.jwtRepository.saveAll(jwtList);
    }

    public Map<String, String> upsertUser(OAuth2User oAuth2User){
        Map<String, String> tmap = new HashMap<>();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        //User existingUser = (User) this.userService.loadUserByUsername(oAuth2User.getAttribute("email"));
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (oAuth2User instanceof OidcUser oidcUser) {
            tmap.put("token", oidcUser.getIdToken().getTokenValue());
        }

        if (existingUser.isEmpty()) {
            User user = new User();
            Role role = Role.builder()
                    .libelle(TypeDeRole.USER)
                    .build();
            user.setEmail(email);
            user.setName(name);
            user.setRole(role);
            user.setActif(true);
            user = userRepository.save(user);
            Jwt jwt = Jwt
                    .builder()
                    .valeur(tmap.get("token"))
                    .desactive(false)
                    .expire(false)
                    .user(user)
                    //.refreshToken(refreshToken)//-----------------Branch refresh-token
                    .build();
            this.jwtRepository.save(jwt);
        }else {
            Jwt existingJwt = jwtRepository.findByUser(existingUser.get());
            existingJwt.setValeur(tmap.get("token"));
            jwtRepository.save(existingJwt);
        }
        System.out.println("Map :: " + tmap);
        return tmap;
    }

    private Map<String, String> generateJwt(User user) {
        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime + 10*60*1000;
        Map<String, Object> claims = Map.of(
                "nom", user.getName(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, user.getEmail()
        );

        String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(user.getEmail())
                .claims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of(BEARER, bearer);
    }

    private Key getKey(){
        byte[] decoded = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoded);
    }

    public ResponseEntity<?> deconnexion() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//Recupération du user connecté
        Jwt jwt = this.jwtRepository.findUserValidToken(user.getEmail(), false, false)
                .orElseThrow(()-> new RuntimeException("Token invalide"));
        jwt.setExpire(true);
        jwt.setDesactive(true);
        this.jwtRepository.save(jwt);

        return ResponseEntity.ok("User "+ user.getName()+" disconnected");
    }

    public Jwt tokenByValue(String token) {
        return this.jwtRepository.findByValeurAndDesactiveAndExpire(token, false, false)
                .orElseThrow(() -> new RuntimeException("Token invalid ou inconnu"));
    }

    public Boolean isTokenExpred(String token) {
        try {
            Date expirationDate = this.getClaims(token, Claims::getExpiration);
            return expirationDate.before(new Date());
        } catch (Exception e) {
            throw new RuntimeException("Le token a expiré");
        }
    }

    private <T> T getClaims(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return this.getClaims(token, Claims::getSubject);
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void removeUselessJwt(){
        log.info("Suppresion des tokens a {} " + Instant.now());
        List<Jwt> tokens = this.jwtRepository.deleteAllByExpireAndDesactiveJwt(true, true);
        if (!tokens.isEmpty()) {
            jwtRepository.deleteAll(tokens);
            log.info("{} tokens supprimés.", tokens.size());
        } else {
            log.info("Aucun token à supprimer.");
        }
    }

    public Map<String, String> refreshToken(Map<String, String> refreshTokenRequest) {
        final Jwt jwt = this.jwtRepository.findByRefreshToken(refreshTokenRequest.get(REFRESH))
                .orElseThrow(() -> new RuntimeException("### Token invalid ###"));
        if (jwt.getRefreshToken().getExpire() || jwt.getRefreshToken().getExpiration().isBefore(Instant.now())) {
            throw new RuntimeException("Token invalid");
        }
        Map<String, String> tokens = this.generate(jwt.getUser().getEmail());
        return tokens;
    }
}
