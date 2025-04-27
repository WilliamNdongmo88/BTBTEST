package will.dev.BTBTEST.services;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import will.dev.BTBTEST.entity.Product;

import java.util.ArrayList;
import java.util.List;

@Component
@SessionScope
public class CartService {
    private List<Product> panier = new ArrayList<>();

    public void ajouterProduit(Product product) {
        panier.add(product);
        System.out.println("Liste de panier ::" + panier);
    }

    public List<Product> getPanier() {
        return panier;
    }
}
