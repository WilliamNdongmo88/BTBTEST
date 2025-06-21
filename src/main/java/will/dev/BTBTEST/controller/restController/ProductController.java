package will.dev.BTBTEST.controller.restController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import will.dev.BTBTEST.dto.ProductDTO;
import will.dev.BTBTEST.entity.Product;
import will.dev.BTBTEST.services.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "product", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    //Post
    @PreAuthorize("hasAnyAuthority('ADMIN_CREATE')")
    @PostMapping("create")
    public void create(@RequestBody Product product){
        this.productService.create(product);
    }

    //Get All Product
    @PreAuthorize("hasAnyAuthority('ADMIN_READ','MANAGER_READ','USER_READ')")
    @GetMapping("all_product")
    public List<ProductDTO> getAllProduct(){
        return this.productService.search();
    }

    //Get Product Id
    @PreAuthorize("hasAnyAuthority('ADMIN_READ','MANAGER_READ','USER_READ')")
    @GetMapping("{id}")
    public Optional<ProductDTO> getProduct(@PathVariable Long id){
        return this.productService.lire(id);
    }

    //Get Product Name
//    @PreAuthorize("hasAnyAuthority('ADMIN_READ','MANAGER_READ','USER_READ')")
//    @GetMapping("product-name")
//    public List<Product> getProduct(@RequestBody Map<String,String> param){
//        return this.productService.searchByName(param.get("name"));
//    }

    //Get Product Name
    @PreAuthorize("hasAnyAuthority('ADMIN_READ','MANAGER_READ','USER_READ')")
    @GetMapping("product-name")
    public List<Product> getProductName(@RequestParam String name){
        return this.productService.searchByName(name);
    }

    //Put
    @PreAuthorize("hasAnyAuthority('ADMIN_UPDATE','MANAGER_UPDATE')")
    @PutMapping("{id}")
    public ResponseEntity<?> putProduct(@PathVariable Long id, @RequestBody Product product){
        return this.productService.modifier(id, product);
    }

    //Delete
    @PreAuthorize("hasAnyAuthority('ADMIN_DELETE')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        return this.productService.delete(id);
    }

}
