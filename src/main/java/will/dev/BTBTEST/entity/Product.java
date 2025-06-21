package will.dev.BTBTEST.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_id")
    private Files productImage;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Files> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "added_by")
    private User addedBy;

    public Product(Long id,String name, Double price, Files productImage, String description, User addedBy) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.productImage = productImage;
        this.description = description;
        this.addedBy = addedBy;
    }

    // méthode utilitaire pour ajouter une image
    public void addImage(Files image) {
        image.setProduct(this);
        this.images.add(image);
    }
}
