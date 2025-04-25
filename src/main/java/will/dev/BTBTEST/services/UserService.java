package will.dev.BTBTEST.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import will.dev.BTBTEST.entity.Role;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.entity.Validation;
import will.dev.BTBTEST.enums.TypeDeRole;
import will.dev.BTBTEST.repository.UserRepository;
import will.dev.BTBTEST.repository.ValidationRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final ValidationRepository validationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //Inscription
    public ResponseEntity<?> create(User user){
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new RuntimeException("Email invalide");
        }

        Optional<User> optionalUser = this.userRepository.findByEmail(user.getEmail());
        if (!optionalUser.isEmpty()) {
            throw new RuntimeException("Email déjà existant");
        }
        user.setActif(false);
        String mdpCrypte = this.bCryptPasswordEncoder.encode(user.getPassword());
        user.setMdp(mdpCrypte);

        Role userRole = new Role();
        userRole.setLibelle(TypeDeRole.USER);
        user.setRole(userRole);
        user = this.userRepository.save(user);
        this.validationService.enregistrer(user);

        return ResponseEntity.ok(user);
    }

    //Activer Compte
    public ResponseEntity<?> activation(Map<String, String> activation) {
        Validation validation = this.validationService.lireCode(activation.get("code"));
        if (Instant.now().isAfter(validation.getExpiration())) {
            throw new RuntimeException("Votre code a expiré");
        }
        User userActiver = this.userRepository.findById(validation.getUser().getId()).orElseThrow(() -> new RuntimeException("Utilisateur inconnu"));
        userActiver.setActif(true);
        Instant creation = Instant.now();
        validation.setActivation(creation);
        validation.setValidationDay(LocalDate.now(ZoneId.systemDefault()));
        this.validationRepository.save(validation);
        this.userRepository.save(userActiver);

        return ResponseEntity.ok("User " + userActiver.getName() + " Activated");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("Aucun utilisateur ne correspond a cet identifiant"));
    }

    public void modifierPassword(Map<String, String> parametre) {
        User user = (User) this.loadUserByUsername(parametre.get("email"));
        this.validationService.enregistrer(user);
    }

}
