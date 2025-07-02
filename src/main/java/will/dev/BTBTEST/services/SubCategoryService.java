package will.dev.BTBTEST.services;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import will.dev.BTBTEST.dto.SubCategoryDto;
import will.dev.BTBTEST.dtoMapper.SubCategoryDtoMapper;
import will.dev.BTBTEST.entity.SubCategory;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.repository.CategoryRepository;
import will.dev.BTBTEST.repository.ProductRepository;
import will.dev.BTBTEST.repository.SubCategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubCategoryService {
    private final SubCategoryRepository subCategoryRepository;
    private final SubCategoryDtoMapper subCategoryDtoMapper;

    public ResponseEntity<?> creerSubCategory(SubCategory subCategory){
        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        subCategory.setAddedBy(userConnected);
        SubCategory subCategoryDansBD = this.subCategoryRepository.findByTitle(subCategory.getType());
        if (subCategoryDansBD ==  null ){
            this.subCategoryRepository.save(subCategory);
            return ResponseEntity.ok(subCategoryDtoMapper.toSubCategoryDto(subCategory));
        }else {
            throw new RuntimeException ("La sous-Catégorie '" + subCategory.getType() + "' existe déjà.");
        }
    }

    public List<SubCategoryDto> rechercher(){
        List<SubCategory> allSubCategory = this.subCategoryRepository.findAll();
        List<SubCategoryDto> subCategoryDtoList = new ArrayList<>();
        for (SubCategory subCategory : allSubCategory){
            subCategoryDtoList.add(subCategoryDtoMapper.toSubCategoryDto(subCategory));
        }
        return subCategoryDtoList;
    }

    public SubCategory lire(Long id) {
        Optional<SubCategory> optionalSubCategory =  this.subCategoryRepository.findById(id);
        return optionalSubCategory.orElseThrow(() -> new EntityNotFoundException("Aucune sous-categorie n'existe avec cette id"));
    }

    public ResponseEntity<?> getSubCatRelatedToCat(Long id) {
        List<SubCategory> subCategoryListSubCategory =  this.subCategoryRepository.findByCategoryId(id);
        if (subCategoryListSubCategory.isEmpty()) throw new EntityNotFoundException("Aucune sous-categorie n'existe avec cette id");
        List<SubCategoryDto> subCategoryDtoList = new ArrayList<>();
        for (SubCategory subCategory : subCategoryListSubCategory){
            subCategoryDtoList.add(subCategoryDtoMapper.toSubCategoryDto(subCategory));
        }
        return ResponseEntity.ok(subCategoryDtoList);
    }

    public List<SubCategory> getSubCategoryByCategory(String title) {
        List<SubCategory> subCategoryDansBD = subCategoryRepository.findByCategoryTitle(title);
        System.out.println("subCategoryDansBD Title"+ subCategoryDansBD.get(0).getTitle());
        if (subCategoryDansBD == null) {
            throw new EntityNotFoundException("Aucune sous-catégorie n'existe avec ce nom : " + title);
        }
        return subCategoryDansBD;
    }

    // Update
    public SubCategory updateSubCategory(Long id, SubCategory subCategoryDetails) {
        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        subCategoryDetails.setUpdateBy(userConnected);
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubCategory not found with id " + id));

        subCategory.setCategory(subCategoryDetails.getCategory());
        subCategory.setTitle(subCategoryDetails.getTitle());
        subCategory.setSlug(subCategoryDetails.getSlug());
        subCategory.setType(subCategoryDetails.getType());
        subCategory.setDeletedAt(subCategoryDetails.getDeletedAt());
        subCategory.setDeletedBy(subCategoryDetails.getDeletedBy());

        return subCategoryRepository.save(subCategory);
    }

    // Delete
    public void deleteSubCategory(Long id) {
        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubCategory not found with id " + id));

        subCategory.setDeletedBy(userConnected);
        subCategoryRepository.delete(subCategory);
    }
}

