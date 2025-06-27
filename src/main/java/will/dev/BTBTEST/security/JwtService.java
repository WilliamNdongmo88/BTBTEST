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
import org.springframework.stereotype.Service;
import will.dev.BTBTEST.dto.UserDto;
import will.dev.BTBTEST.dtoMapper.UserDtoMapper;
import will.dev.BTBTEST.entity.Jwt;
import will.dev.BTBTEST.entity.RefreshToken;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.repository.JwtRepository;
import will.dev.BTBTEST.repository.RoleRepository;
import will.dev.BTBTEST.repository.UserRepository;
import will.dev.BTBTEST.services.UserService;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.*;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtRepository jwtRepository;
    private final UserDtoMapper userDtoMapper;
    public static final String BEARER = "Bearer";
    public static final String REFRESH = "refresh";

    public Map<String, String> generate(String username) {
        // 1) charge l’utilisateur
        User user = (User) userService.loadUserByUsername(username);

        // 2) désactive tous les anciens tokens de cet utilisateur
        disableTokens(user);

        // 3) génère un nouveau JWT signé
        Map<String, String> jwtMap = new HashMap<>( generateJwt(user) );

        // 4) crée un RefreshToken
        RefreshToken refreshToken = RefreshToken.builder()
                .valeur(UUID.randomUUID().toString())
                .expire(false)
                .creation(Instant.now())
                .expiration(Instant.now().plus(48, ChronoUnit.HOURS))
                .build();

        // 5) crée et sauve un objet Jwt en base (avec la valeur du token et le refreshToken)
        Jwt jwt = Jwt.builder()
                .valeur(jwtMap.get(BEARER))
                .desactive(false)
                .expire(false)
                .user(user)
                .refreshToken(refreshToken)
                .build();
        jwtRepository.save(jwt);

        // 6) retourne le map contenant { "Bearer": <token>, "refresh": <refreshValue> }
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

    private Map<String, String> generateJwt(User user) {
        long now       = System.currentTimeMillis();
        long expireIn  = now + 100 * 60 * 1000; // 100 minutes

        Map<String, Object> claims = Map.of(
                "nom", user.getName(),
                "role", user.getRole().getLibelle(),
                Claims.EXPIRATION, new Date(expireIn),
                Claims.SUBJECT, user.getEmail()
        );

        String token = Jwts.builder()
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expireIn))
                .setSubject(user.getEmail())
                .addClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        return Map.of(BEARER, token);
    }

    private Key getKey() {
        // Clé Base64 fixée en dur
        String ENCRYPTION_KEY = "9deacf9b9645cd4739ce7db841635e3665fa217ef413e7087649c23f6528cd66";
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

        UserDto userDto = userDtoMapper.mapToDto(user);

        return ResponseEntity.ok(userDto);
    }

    public Jwt tokenByValue(String token) {
        // Recherche du JWT en base, non désactivé et non expiré
        return jwtRepository.findByValeurAndDesactiveAndExpire(token, false, false)
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
        final String tokenValeur = refreshTokenRequest.get("refresh");
        log.info("Token reçu : {}", tokenValeur);

        final Jwt jwt = this.jwtRepository.findByRefreshToken(tokenValeur)
                .orElseThrow(() -> new RuntimeException("### Token invalid ###"));

        final RefreshToken refreshToken = jwt.getRefreshToken();
        log.info("Expire flag: {}", refreshToken.getExpire());
        log.info("Expiration: {}", refreshToken.getExpiration());
        log.info("Instant.now(): {}", Instant.now());

        if (Boolean.TRUE.equals(refreshToken.getExpire()) || refreshToken.getExpiration().isBefore(Instant.now())) {
            throw new RuntimeException("Token invalid");
        }

        return this.generate(jwt.getUser().getEmail());
    }

}
