package will.dev.BTBTEST.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import will.dev.BTBTEST.dto.ProductDTO;
import will.dev.BTBTEST.dtoMapper.ProductDTOMapper;
import will.dev.BTBTEST.entity.Files;
import will.dev.BTBTEST.entity.Product;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.repository.FilesRepository;
import will.dev.BTBTEST.repository.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final FilesRepository filesRepository;
    private final ProductDTOMapper productDTOMapper;

    //Create
    public void create(Product product) {
        Optional<Product> productDansBD = this.productRepository.findByName(product.getName());
        if (!productDansBD.isEmpty()) {
            throw new RuntimeException("Product already exist");
        }
        Files file = new Files();
        file.setName(product.getProductImage().getName());
        file.setContent(product.getProductImage().getContent());
        this.filesRepository.save(file);

        if(!product.getImages().isEmpty()){
            for (int i = 0; i < product.getImages().size(); i++) {
                Files files = new Files();
                files.setName(product.getImages().get(i).getName());
                files.setContent(product.getImages().get(i).getContent());
                product.addImage(files);
            }
        }else {throw new RuntimeException("Liste d'images vide");}

        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        product.setAddedBy(userConnected);
        product.setProductImage(file);

        this.productRepository.save(product);
    }

    //Read
    public List<ProductDTO> search() {
        List<Product> products = (List<Product>) this.productRepository.findAll();
        List<ProductDTO> productDTOList = new java.util.ArrayList<>(List.of());
        for (Product product: products){
            productDTOList.add(productDTOMapper.mapToDto(product));
        }
        return productDTOList;
    }

    //Read
    public Optional<ProductDTO> lire(Long id) {
        Product product = this.productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        ProductDTO productDTO = productDTOMapper.mapToDto(product);
        return Optional.ofNullable(productDTO);
    }

    //Update
    public ResponseEntity<?> modifier(Long id, Product product) {
        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Product productDansBd = this.productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (Objects.equals(productDansBd.getId(), product.getId()) && Objects.equals(productDansBd.getId(), id)){
            productDansBd.setName(product.getName());
            productDansBd.setPrice(product.getPrice());
            productDansBd.setDescription(product.getDescription());
            productDansBd.setAddedBy(userConnected);
            this.productRepository.save(productDansBd);
        }
        return ResponseEntity.ok(product.getName() + " has been updated Successfully ");
    }

    //Delete
    public ResponseEntity<?> delete(Long id) {
        Product productDansBd = this.productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (productDansBd != null) {
            this.productRepository.deleteById(id);
        }
        return ResponseEntity.ok("Deleted successfully");
    }

    //Get by name
    public List<Product> searchByName(String param) {
        return this.productRepository.findAllProductByName(param);
    }
}
