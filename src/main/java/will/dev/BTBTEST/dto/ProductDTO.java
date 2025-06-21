package will.dev.BTBTEST.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private FileDTO productImage;
    private List<FileDTO> images;
    private UserDto addedBy; // ou UserDto si besoin

    // getters, setters
}
