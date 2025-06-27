package will.dev.BTBTEST.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private FileDTO mainImage; // image principale
    private List<FileDTO> images; // images supplémentaires
}
