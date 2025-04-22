package will.dev.BTBTEST.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import will.dev.BTBTEST.services.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


}
