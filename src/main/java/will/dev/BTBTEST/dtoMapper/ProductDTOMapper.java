package will.dev.BTBTEST.dtoMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import will.dev.BTBTEST.dto.FileDTO;
import will.dev.BTBTEST.dto.ProductDTO;
import will.dev.BTBTEST.dto.UserDto;
import will.dev.BTBTEST.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class ProductDTOMapper {
    // Mapping Entity → DTO
    private final FileDTOMapper fileDTOMapper;

    public ProductDTO mapToDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());

        if (product.getProductImage() != null) {
            dto.setMainImage(fileDTOMapper.map(product.getProductImage()));
        }

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            List<FileDTO> imageDtos = product.getImages()
                    .stream()
                    .map(fileDTOMapper::map)
                    .collect(Collectors.toList());
            dto.setImages(imageDtos);
        }

        return dto;
    }
}

