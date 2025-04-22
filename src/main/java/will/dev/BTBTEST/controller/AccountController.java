package will.dev.BTBTEST.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.services.UserService;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final UserService userService;

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
}
