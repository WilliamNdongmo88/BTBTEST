package will.dev.BTBTEST.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String price;

    public Product(String name, String price, String description, User addedBy) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.addedBy = addedBy;
    }

    private String description;

    @ManyToOne
    @JoinColumn(name = "added_by")
    private User addedBy;

}
