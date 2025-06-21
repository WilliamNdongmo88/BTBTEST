package will.dev.BTBTEST.dtoMapper;

import org.springframework.context.annotation.Configuration;
import will.dev.BTBTEST.dto.FileDTO;
import will.dev.BTBTEST.dto.ProductDTO;
import will.dev.BTBTEST.dto.UserDto;
import will.dev.BTBTEST.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ProductDTOMapper {
    // Mapping Entity → DTO
    public ProductDTO mapToDto(Product entity) {
        ProductDTO dto = new ProductDTO();

        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(entity.getProductImage().getId());
        fileDTO.setName(entity.getProductImage().getName());
        fileDTO.setContent(entity.getProductImage().getContent());

        // Ajout des images
        List<FileDTO> imageDtos = entity.getImages().stream().map(image -> {
            FileDTO f = new FileDTO();
            f.setId(image.getId());
            f.setName(image.getName());
            f.setTemp(image.getTemp());
            f.setContent(image.getContent());
            return f;
        }).collect(Collectors.toList());

        UserDto userDto = new UserDto();
        userDto.setId(entity.getAddedBy().getId());
        userDto.setName(entity.getAddedBy().getName());
        userDto.setEmail(entity.getAddedBy().getEmail());

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setProductImage(fileDTO);
        dto.setImages(imageDtos);
        dto.setAddedBy(userDto);
        return dto;
    }

}

