package will.dev.BTBTEST.controller.restController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import will.dev.BTBTEST.dto.UserDto;
import will.dev.BTBTEST.dtoMapper.UserDtoMapper;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.services.UserService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;


    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public Principal user(Principal user){
        return user;//Object user =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/session/me")
    public ResponseEntity<?> getConnectedUser(){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        return "L'utilisateur connecté est : " + auth.getName();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDto userDto = userDtoMapper.mapToDto(user);
        UserDto connectedUser = userService.getUser(userDto.getEmail());
        return ResponseEntity.ok("user : " + connectedUser);
    }

}
