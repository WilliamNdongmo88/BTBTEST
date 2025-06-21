package will.dev.BTBTEST.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import will.dev.BTBTEST.dto.UserDto;
import will.dev.BTBTEST.dtoMapper.UserDtoMapper;
import will.dev.BTBTEST.entity.EmailValids;
import will.dev.BTBTEST.entity.Role;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.entity.Validation;
import will.dev.BTBTEST.enums.TypeDeRole;
import will.dev.BTBTEST.repository.EmailValidsRepository;
import will.dev.BTBTEST.repository.UserRepository;
import will.dev.BTBTEST.repository.ValidationRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final ValidationRepository validationRepository;
    private final EmailValidsRepository emailValidsRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDtoMapper userDtoMapper;

    //Inscription
    public ResponseEntity<UserDto> create(UserDto userDto){
        if (!userDto.getEmail().contains("@") || !userDto.getEmail().contains(".")) {
            throw new RuntimeException("Email invalide: '@' ou '.' requis");
        }
        User user = userDtoMapper.mapToEntity(userDto);
        Optional<User> optionalUser = this.userRepository.findByEmail(user.getEmail());
        if (!optionalUser.isEmpty()) {
            throw new RuntimeException("Email déjà existant");
        }
        user.setActif(false);
        String mdpCrypte = this.bCryptPasswordEncoder.encode(userDto.getPassword());
        user.setMdp(mdpCrypte);

//        if (user.getEmail().contains(".admin")){
//            Optional<EmailValids> emailValids = Optional.ofNullable(this.emailValidsRepository.findByEmail(user.getEmail())
//                    .orElseThrow(() -> new RuntimeException("Email non valid")));
//            System.out.println("Email: " + emailValids);
//            if (emailValids.isPresent()){
//                //this.bool = true;
//                Role userRole = new Role();
//                userRole.setLibelle(TypeDeRole.ADMIN);
//                user.setRole(userRole);
//                user = this.userRepository.save(user);
//                this.validationService.enregistrer(user);
//            }
//        } else if (user.getEmail().contains(".manager")) {
//            Optional<EmailValids> emailValids = Optional.ofNullable(this.emailValidsRepository.findByEmail(user.getEmail())
//                    .orElseThrow(() -> new RuntimeException("Email non valid")));
//            System.out.println("Email: " + emailValids);
//            if (emailValids.isPresent()){
//                //this.bool = true;
//                Role userRole = new Role();
//                userRole.setLibelle(TypeDeRole.MANAGER);
//                user.setRole(userRole);
//                user = this.userRepository.save(user);
//                this.validationService.enregistrer(user);
//            }
//        }else {

            Role userRole = new Role();
            userRole.setLibelle(TypeDeRole.USER);
            user.setRole(userRole);
            user = this.userRepository.save(user);
            this.validationService.enregistrer(user);
        //}
        return ResponseEntity.ok(userDtoMapper.mapToDto(user));
    }

    //Inscription
    public ResponseEntity<User> createFromGoogle(String name, String email, Boolean emailVerified){
        User user =  new User();
        Role userRole = new Role();
        userRole.setLibelle(TypeDeRole.USER);
        user = User.builder()
                   .name(name)
                   .email(email)
                   .actif(emailVerified)
                   .role(userRole)
                   .build();
        user = this.userRepository.save(user);
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


    public void newPassword(Map<String, String> param) {
        User user = (User) this.loadUserByUsername(param.get("email"));
        final Validation validation = validationService.lireCode(param.get("code"));
        if (validation.getUser().getEmail().equals(user.getEmail()) && validation.getUser().getId().equals(user.getId())){
            String mdpCrypte = this.bCryptPasswordEncoder.encode(param.get("password"));
            user.setMdp(mdpCrypte);
            this.userRepository.save(user);
        }else {
            throw new RuntimeException("Code activation incorrect pour cet utilisateur");
        }
    }
    public List<User> listUsers() {
        return this.userRepository.findAll();
    }

    public ResponseEntity<Map<String, Object>> getAccountCreateToDay(){
        LocalDate today = LocalDate.now();
        //LocalDate today = LocalDate.of(2025, 3, 21);
        System.out.println("Date du jour : " + today);
        List<User> users = this.userRepository.findByCreateDay(today);
        Map<String, Object> response = new HashMap<>();
        response.put("date", today.toString());
        response.put("totalAccountsCreated", users.size());
        response.put("users", users);  // Optionnel, si tu veux renvoyer la liste des utilisateurs

        return ResponseEntity.ok(response);
    }

    public UserDto getUser(String username) {
        User user = (User) this.loadUserByUsername(username);
        return userDtoMapper.mapToDto(user);
    }

    public User findOrCreateUser(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    return userRepository.save(newUser);
                });
    }

    public ResponseEntity<?> checkEmail(String email) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Adresse email inexistante"));
        return ResponseEntity.ok(userDtoMapper.mapToDto(user));
    }
}
