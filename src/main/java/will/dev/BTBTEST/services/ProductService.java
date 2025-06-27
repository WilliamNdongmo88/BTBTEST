package will.dev.BTBTEST.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import will.dev.BTBTEST.controller.restController.FilesController;
import will.dev.BTBTEST.dto.FileDTO;
import will.dev.BTBTEST.dto.ProductDTO;
import will.dev.BTBTEST.dtoMapper.ProductDTOMapper;
import will.dev.BTBTEST.entity.FileParams;
import will.dev.BTBTEST.entity.Product;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.repository.FilesRepository;
import will.dev.BTBTEST.repository.ProductRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final FilesRepository filesRepository;
    private final ProductDTOMapper productDTOMapper;
    private final String basePath;

    public ProductService(
            @Value("${application.files.base-path}") final String basePath,
            ProductRepository productRepository, ProductDTOMapper productDTOMapper,
            FilesRepository filesRepository) {
        this.productRepository = productRepository;
        this.productDTOMapper = productDTOMapper;
        this.filesRepository = filesRepository;
        this.basePath = basePath;
    }

    //Create
    @Transactional(rollbackFor = Exception.class)
    public void create(Product product) throws IOException {
        // Vérifie si le produit existe déjà
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new RuntimeException("Produit déjà existant avec ce nom");
        }

        // 🔐 Récupération de l'utilisateur connecté
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User)) {
            throw new RuntimeException("Utilisateur connecté introuvable ou invalide");
        }
        User userConnected = (User) principal;
        product.setAddedBy(userConnected);

        // ✅ Traitement de l’image principale
        will.dev.BTBTEST.entity.Files imagePrincipale = product.getProductImage();
        if (imagePrincipale != null && imagePrincipale.getName() != null) {
            String extension = FilenameUtils.getExtension(imagePrincipale.getName());
            String temp = System.currentTimeMillis() + "." + extension;
            imagePrincipale.setTemp(temp);
            imagePrincipale.setProduct(product);
            filesRepository.save(imagePrincipale);
            writeOnDisk(imagePrincipale);
            product.setProductImage(imagePrincipale);
        } else {
            throw new RuntimeException("Image principale manquante ou invalide");
        }

        // ✅ Traitement des images supplémentaires
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            List<will.dev.BTBTEST.entity.Files> extraImages = new ArrayList<>();
            for (will.dev.BTBTEST.entity.Files img : product.getImages()) {
                if (img.getName() != null) {
                    String ext = FilenameUtils.getExtension(img.getName());
                    String tempName = System.currentTimeMillis() + "." + ext;
                    img.setTemp(tempName);

                    img.setProduct(product);
                    extraImages.add(img);

                    filesRepository.save(img);
                    writeOnDisk(img);
                } else {
                    throw new RuntimeException("Une des images supplémentaires est invalide");
                }
            }
            product.setImages(extraImages);
        } else {
            throw new RuntimeException("Liste des images supplémentaires vide");
        }

        // ✅ Sauvegarde du produit
        Product saved = productRepository.save(product);
        System.out.println("✅ Produit enregistré avec ID : " + saved.getId());
    }

    private void writeOnDisk(will.dev.BTBTEST.entity.Files file) throws IOException {
        String fullPath = String.format("%s/%s", basePath, file.getTemp());
        Path folder = Paths.get(fullPath).getParent();
        Files.createDirectories(folder);

        String base64Data = file.getContent();
        if (base64Data.contains(",")) {
            base64Data = base64Data.split(",")[1]; // Supprime le préfixe base64
        }

        byte[] decodedFile = Base64.getDecoder().decode(base64Data);
        File destinationFile = new File(fullPath);

        if (destinationFile.exists()) {
            FileUtils.deleteQuietly(destinationFile);
        }

        FileUtils.writeByteArrayToFile(destinationFile, decodedFile);
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
    @Transactional(rollbackFor = Exception.class)
    public void modifier(Long id, Product product) throws IOException {
        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Product productDansBd = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // ✏️ MAJ des infos principales
        productDansBd.setName(product.getName());
        productDansBd.setPrice(product.getPrice());
        productDansBd.setDescription(product.getDescription());
        productDansBd.setAddedBy(userConnected);

        // 🔁 MAJ de l'image principale
        will.dev.BTBTEST.entity.Files oldMainImage = productDansBd.getProductImage();
        if (oldMainImage != null) {
            filesRepository.delete(oldMainImage);
        }

        will.dev.BTBTEST.entity.Files newMainImage = product.getProductImage();
        if (newMainImage != null && newMainImage.getName() != null) {
            String ext = FilenameUtils.getExtension(newMainImage.getName());
            String temp = System.currentTimeMillis() + "." + ext;
            newMainImage.setTemp(temp);
            newMainImage.setProduct(productDansBd); //

            will.dev.BTBTEST.entity.Files savedMainImage = filesRepository.save(newMainImage);
            writeOnDisk(savedMainImage);

            productDansBd.setProductImage(savedMainImage);
        }

        // 🆕 Ajouter les nouvelles images
        List<will.dev.BTBTEST.entity.Files> imagesFinales = new ArrayList<>();
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            for (will.dev.BTBTEST.entity.Files img : product.getImages()) {
                if (img.getName() != null) {
                    String ext = FilenameUtils.getExtension(img.getName());
                    String temp = System.currentTimeMillis() + "." + ext;
                    img.setTemp(temp);
                    img.setProduct(productDansBd);

                    will.dev.BTBTEST.entity.Files saved = filesRepository.save(img);
                    writeOnDisk(saved);
                    imagesFinales.add(saved);
                }
            }
        }

        // 🔄 Supprimer les anciennes images associées au produit
        List<will.dev.BTBTEST.entity.Files> oldImages = productDansBd.getImages();
        for (will.dev.BTBTEST.entity.Files oldImg : oldImages) {
            for(will.dev.BTBTEST.entity.Files img  : product.getImages()){
                if(!Objects.equals(img.getTemp(), oldImg.getTemp())){
                    filesRepository.delete(oldImg);
                }
            }
        }
        productDansBd.getImages().clear();

        productDansBd.setImages(imagesFinales); // 🔁 important

        // ✅ Enfin, sauvegarde du produit
        productRepository.save(productDansBd);

        //return ResponseEntity.ok(productDansBd.getName() + " a été mis à jour avec succès");
    }

    //Delete
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            for (will.dev.BTBTEST.entity.Files image : product.getImages()) {
                deleteFromDisk(image.getTemp());
                filesRepository.delete(image);
            }
            product.getImages().clear();
        }

        will.dev.BTBTEST.entity.Files mainImage = product.getProductImage();
        if (mainImage != null) {
            deleteFromDisk(mainImage.getTemp());
            filesRepository.delete(mainImage);
            product.setProductImage(null);
        }

        productRepository.delete(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProducts(List<Long> ids) throws IOException {
        for (Long id : ids) {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'ID : " + id));

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                for (will.dev.BTBTEST.entity.Files image : product.getImages()) {
                    deleteFromDisk(image.getTemp());
                    filesRepository.delete(image);
                }
                product.getImages().clear();
            }

            will.dev.BTBTEST.entity.Files mainImage = product.getProductImage();
            if (mainImage != null) {
                deleteFromDisk(mainImage.getTemp());
                filesRepository.delete(mainImage);
                product.setProductImage(null);
            }

            productRepository.delete(product);
        }
    }


    private void deleteFromDisk(String temp) throws IOException {
        if (temp == null) return;

        String path = basePath + "/" + temp;
        File file = new File(path);
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
    }


    //Get by name
    public List<Product> searchByName(String param) {
        return this.productRepository.findAllProductByName(param);
    }
}
