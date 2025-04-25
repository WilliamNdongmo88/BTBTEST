package will.dev.BTBTEST.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Map;

@Controller
public class ViewController {

    @GetMapping("/")
    public String helloWord(){
        return "home";
    }

    @GetMapping("/profile")
    public String userProfile(OAuth2AuthenticationToken authentication, Model model) {
        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();

        model.addAttribute("name", attributes.get("name"));
        model.addAttribute("email", attributes.get("email"));
        model.addAttribute("photo", attributes.get("picture"));

        return "profile";
    }
}
