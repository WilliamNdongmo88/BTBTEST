package will.dev.BTBTEST.controller;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import will.dev.BTBTEST.entity.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import will.dev.BTBTEST.entity.Product;
import will.dev.BTBTEST.repository.ProductRepository;
import will.dev.BTBTEST.repository.UserRepository;
import will.dev.BTBTEST.services.CartService;
import will.dev.BTBTEST.services.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
public class ViewProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartService cartService;
    private static Double TOTAL = 0.0;

    public ViewProductController(ProductService productService, UserRepository userRepository, ProductRepository productRepository, CartService cartService, Double TOTAL) {
        this.productService = productService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    public ViewProductController() {}

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

    @GetMapping("/page-products")
    public String afficherProducts(Model model, OAuth2AuthenticationToken authentication) {
        OAuth2User oauthUser = authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        Optional<User> userConnected = Optional.of(this.userRepository.
                findByEmail(email).orElseThrow(() -> new RuntimeException(("email not found"))));
        List<Product> prodList = (List<Product>) this.productRepository.findAll();
        System.out.println("Product :: "+ prodList);
        List<Product> products = new ArrayList<>();
        for(Product prod: prodList){
            products.add(
                    new Product(
                            prod.getId(),
                            prod.getName(),
                            prod.getPrice(),
                            prod.getImageUrl(),
                            prod.getDescription(),
                            userConnected.get())
            );
        }
        System.out.println("products :: "+ products);
        model.addAttribute("products", products);
        return "/product/product";
    }

    @GetMapping("/page-products/{id}")
    public String afficherDetailProduit(@PathVariable Long id, Model model) {
        try{
            Optional<Product> product = this.productService.lire(id);
            if (product.isPresent()) {
                model.addAttribute("product", product.get());
            } else {
                return "redirect:/error/error"; //  page d'erreur
            }
            return "/product/product-detail"; // product-detail.html
        } catch (Exception e) {
            log.info("Error occurent ::{}", e.getMessage());
        }
        return "/error/error";//Ne fonctionne pas
    }

    @PostMapping("/panier/ajouter")
    public String ajouterAuPanier(@RequestParam Long id) {
        Optional<Product> product = this.productService.lire(id);
        this.cartService.ajouterProduit(product.get());
        return "redirect:/panier"; // ou redirect:/panier pour afficher le panier
    }

    @GetMapping("/panier")
    public String voirPanier(Model model) {
        List<Product> panier = this.cartService.getPanier();
        double total = panier.stream().mapToDouble(Product::getPrice).sum();
        TOTAL = total;
        model.addAttribute("panier", panier);
        model.addAttribute("total", total);
        return "/panier/panier"; // panier.html
    }

    public Double totalPrice(){
        log.info("TOTAL :: "+ TOTAL);
        return TOTAL;
    }

}
