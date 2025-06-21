package will.dev.BTBTEST.controller.restController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import will.dev.BTBTEST.dto.AuthenticationDTO;
import will.dev.BTBTEST.dto.UserDto;
import will.dev.BTBTEST.classUtiles.TokenUserResponse;
import will.dev.BTBTEST.dtoMapper.UserDtoMapper;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.repository.UserRepository;
import will.dev.BTBTEST.security.JwtService;
import will.dev.BTBTEST.services.UserService;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;

    //Register
    @PostMapping("/inscription")
    public ResponseEntity<UserDto> inscription(@Valid @RequestBody UserDto userDto){
        return this.userService.create(userDto);
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
    public ResponseEntity<?> connexion(@RequestBody AuthenticationDTO authenticationDTO){
        final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationDTO.email(), authenticationDTO.password())
        );

        System.out.println("authenticate isAuthenticated:: " + authenticate.isAuthenticated());
        if (authenticate.isAuthenticated()){
            log.info("username: " + authenticationDTO.email());
            Map<String, String> stringMap = this.jwtService.generate(authenticationDTO.email());
            UserDto userdto = userService.getUser(authenticationDTO.email());
            TokenUserResponse response = new TokenUserResponse(stringMap.get("Bearer"), stringMap.get("refresh"), userdto);
            return ResponseEntity.ok(response);
        }
        return null;
    }

    //Check Email
    @PostMapping("check-email")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> param){
        return this.userService.checkEmail(param.get("email"));
    }

    //Check Password
    @PostMapping("check-password")
    public ResponseEntity<?> checkPassword(@RequestBody Map<String, String> param){
        Optional<User> user = this.userRepository.findByEmail(param.get("email"));
        boolean mdpDeCrypte = this.bCryptPasswordEncoder.matches(param.get("password"), user.get().getPassword());
        if(!mdpDeCrypte){
            throw new RuntimeException("Mot de passe incorrect");
        }
        UserDto userDto = userDtoMapper.mapToDto(user.get());
        AuthenticationDTO authenticationDTO = new AuthenticationDTO(userDto.getEmail(), param.get("password"));
        System.out.println("authenticationDTO "+ authenticationDTO);
        return this.connexion(authenticationDTO);
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
