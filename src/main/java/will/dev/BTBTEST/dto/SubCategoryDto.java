package will.dev.BTBTEST.dto;

import lombok.Data;

@Data
public class SubCategoryDto {
    private Long id;
    private String title;
    private String slug;
    private String type;

    private CategoryDto category;
    //private List<ProductDTO> productDTOList;
    private UserDto addedBy;
}

