package will.dev.BTBTEST.controller.restController;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import will.dev.BTBTEST.classUtiles.GoogleTokenValidator;

import will.dev.BTBTEST.classUtiles.TokenUserResponse;
import will.dev.BTBTEST.dto.UserDto;
import will.dev.BTBTEST.dtoMapper.UserDtoMapper;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.repository.UserRepository;
import will.dev.BTBTEST.security.JwtService;
import will.dev.BTBTEST.services.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class Auth2Controller {
    private final UserService userService;
    private final JwtService jwtService;
    private final GoogleTokenValidator googleTokenValidator;
    private final UserDtoMapper userDtoMapper;
    private final UserRepository userRepository;

    public Auth2Controller(UserService userService, JwtService jwtService,
                           GoogleTokenValidator googleTokenValidator, UserDtoMapper userDtoMapper,
                           UserRepository userRepository
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.googleTokenValidator = googleTokenValidator;
        this.userDtoMapper = userDtoMapper;
        this.userRepository = userRepository;
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody Map<String, String> payload) throws IOException {
        String idToken = payload.get("idToken");
        // ———————————— Validation Google existante ————————————
        Map<String, Object> googlePayload = googleTokenValidator.validateToken(idToken);
        String name = (String) googlePayload.get("name");
        String email = (String) googlePayload.get("email");
        Boolean emailVerified = Boolean.valueOf((String) googlePayload.get("email_verified"));

        if (email == null || !emailVerified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email non vérifié"));
        }

        // Charge l’utilisateur (UserDetails) (cree un nouveau si non trouvé)
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        UserDto userdto = new UserDto();
        User user = new User();
        if (optionalUser.isPresent()){
            user = (User) userService.loadUserByUsername(email);
            userdto = userDtoMapper.mapToDto(user);
        }else {
            userService.createFromGoogle(name, email, emailVerified).getBody();
            user = (User) userService.loadUserByUsername(email);
            userdto = userDtoMapper.mapToDto(user);
        }

        // ===> On appelle generate(username) pour persister en base
        Map<String, String> jwtMap = jwtService.generate(email);
        String jwt = jwtMap.get(JwtService.BEARER);      // la valeur "Bearer"
        String refresh = jwtMap.get(JwtService.REFRESH); // le refreshToken

        // On renvoie le token dans la réponse JSON
        Map<String, String> token = Map.of(
                "token", jwt,
                "refresh", refresh,
                "email", email,
                "name", user.getUsername()
        );
        TokenUserResponse response = new TokenUserResponse(token.get("token"), token.get("refresh"), userdto);
        System.out.println("token response : "+ response);
        return ResponseEntity.ok(response);
    }
}