package will.dev.BTBTEST.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import will.dev.BTBTEST.services.UserService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public Principal user(Principal user){
        return user;//Object user =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/username")
    public String userName(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "L'utilisateur connecté est : " + auth.getName();
    }

}
