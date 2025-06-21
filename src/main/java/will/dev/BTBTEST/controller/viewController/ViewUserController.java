package will.dev.BTBTEST.controller.viewController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import will.dev.BTBTEST.dto.LoginFormDto;
import will.dev.BTBTEST.dto.UserDto;
import will.dev.BTBTEST.security.JwtService;
import will.dev.BTBTEST.services.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ViewUserController {
    final private UserService userService;
    final private AuthenticationManager authenticationManager;
    final private JwtService jwtService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "/user/register"; // affiche register.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserDto userDto) {
        System.out.println("Nom : " + userDto.getName());
        System.out.println("Email : " + userDto.getEmail());
        System.out.println("Mot de passe : " + userDto.getPassword());
        userService.create(userDto);
        // Redirection ou message de succès
        return "redirect:/success";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginForm", new LoginFormDto());
        return "/user/login"; // login.html
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginForm") LoginFormDto loginFormDto) {
        Map<String, String> tokenData = new HashMap<>();
        // Ici tu peux valider les identifiants :
        System.out.println("Username : " + loginFormDto.getUsername());
        System.out.println("Password : " + loginFormDto.getPassword());

        Authentication authRequest = new UsernamePasswordAuthenticationToken(loginFormDto.getUsername(), loginFormDto.getPassword());
        Authentication authResult = authenticationManager.authenticate(authRequest);

        System.out.println("authenticate isAuthenticated:: " + authResult.isAuthenticated());
        if (authResult.isAuthenticated()){
            log.info("username: " + loginFormDto.getUsername());
            tokenData = this.jwtService.generate(loginFormDto.getUsername());//Retourne le token de connexion
            System.out.println("tokenData : " + tokenData);
        }

        // Redirige vers la page d'accueil après connexion réussie
        return "/product/product";
    }
}
