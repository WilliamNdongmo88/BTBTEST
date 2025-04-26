package will.dev.BTBTEST.controller;
import org.springframework.security.core.context.SecurityContextHolder;
import will.dev.BTBTEST.entity.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import will.dev.BTBTEST.entity.Product;

import java.security.Principal;
import java.util.List;
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

    @GetMapping("/product")
    public String afficherProduits(Model model) {
        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Product> produits = List.of(
                new Product("Casque Bluetooth", "59.99", "/images/casque.jpg",userConnected),
                new Product("Clavier Mécanique", "89.00", "/images/clavier.jpg",userConnected),
                new Product("Souris Gamer", "39.50", "/images/souris.jpg",userConnected)
        );
        model.addAttribute("produits", produits);
        return "product"; // produits.html
    }

}
