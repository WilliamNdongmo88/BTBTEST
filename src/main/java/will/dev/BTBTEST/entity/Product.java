package will.dev.BTBTEST.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name = "image_url")
    private String imageUrl;
    private String description;

    @ManyToOne
    @JoinColumn(name = "added_by")
    private User addedBy;

    public Product(Long id,String name, Double price, String imageUrl, String description, User addedBy) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.addedBy = addedBy;
    }
}
