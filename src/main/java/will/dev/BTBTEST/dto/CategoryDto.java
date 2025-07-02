package will.dev.BTBTEST.dto;

import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String title;
    private String slug;

    private UserDto addedBy;
}

