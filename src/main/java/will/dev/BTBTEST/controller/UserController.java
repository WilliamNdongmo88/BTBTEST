package will.dev.BTBTEST.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import will.dev.BTBTEST.services.UserService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @RequestMapping(path = "/user")
    public Principal user(Principal user){
        return user;
    }

}
