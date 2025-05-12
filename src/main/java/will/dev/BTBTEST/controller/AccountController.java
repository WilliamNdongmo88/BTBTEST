package will.dev.BTBTEST.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import will.dev.BTBTEST.dto.AuthenticationDTO;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.repository.UserRepository;
import will.dev.BTBTEST.security.JwtService;
import will.dev.BTBTEST.services.UserService;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    //Register
    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(@RequestBody User user){
        return this.userService.create(user);
    }

    //Activation
    @PostMapping("activation")
    public ResponseEntity<?> activation(@RequestBody Map<String, String> activation){
        return this.userService.activation(activation);
    }

    //New Activation Code
    @PostMapping("new-activation-code")
    public void newActivationCode(@RequestBody Map<String, String> param){
        this.userService.modifierPassword(param);
    }

    //Connexion
    @PostMapping("connexion")
    public Map<String, String> connexion(@RequestBody AuthenticationDTO authenticationDTO){
//        final Authentication authenticate = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(authenticationDTO.username(), authenticationDTO.password())
//        );

        Authentication authRequest = new UsernamePasswordAuthenticationToken(authenticationDTO.username(), authenticationDTO.password());
        Authentication authResult = authenticationManager.authenticate(authRequest);

        System.out.println("authenticate isAuthenticated:: " + authResult.isAuthenticated());
        if (authResult.isAuthenticated()){
            log.info("username: " + authenticationDTO.username());
            return this.jwtService.generate(authenticationDTO.username());//Retourne le token de connexion
        }
        return null;
    }

    //Modified Password
    @PostMapping("modified-password")
    public void modifierPassword(@RequestBody Map<String, String> param){
        this.userService.modifierPassword(param);
    }

    //New Password
    @PostMapping("new-password")
    public void newPassword(@RequestBody Map<String, String> param){
        this.userService.newPassword(param);
    }

    //deconnexion
    @PostMapping("deconnexion")
    public ResponseEntity<?> deconnexion(){
        return this.jwtService.deconnexion();
    }

    //Refresh token
    @PostMapping("refresh-token")
    public Map<String, String> refreshToken(@RequestBody Map<String, String> refreshTokenRequest){
        return this.jwtService.refreshToken(refreshTokenRequest);
    }

}
