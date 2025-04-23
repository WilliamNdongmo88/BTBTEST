package will.dev.BTBTEST.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.services.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //Get all users
    @PreAuthorize("hasAnyAuthority('ADMIN_READ', 'MANAGER_READ')")
    @GetMapping("all_users")
    public List<User> list(){
        return this.userService.listUsers();
    }

    //Get Account create today
    @PreAuthorize("hasAnyAuthority('ADMIN_READ', 'MANAGER_READ')")
    @GetMapping("account_create_today")
    public ResponseEntity<Map<String, Object>> getAccountCreateToDay(){
        return this.userService.getAccountCreateToDay();
    }
}