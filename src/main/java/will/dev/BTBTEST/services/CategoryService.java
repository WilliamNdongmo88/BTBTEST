package will.dev.BTBTEST.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import will.dev.BTBTEST.dto.CategoryDto;
import will.dev.BTBTEST.dto.SubCategoryDto;
import will.dev.BTBTEST.dtoMapper.SubCategoryDtoMapper;
import will.dev.BTBTEST.entity.Category;
import will.dev.BTBTEST.entity.SubCategory;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SubCategoryDtoMapper subCategoryDtoMapper;

    //Créer category
    public ResponseEntity<?> creerCategory(Category category){
        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        category.setAddedBy(userConnected);
        Category categoryDansBD = this.categoryRepository.findByTitle(category.getTitle());
        if (categoryDansBD ==  null ){
            this.categoryRepository.save(category);
            //return ResponseEntity.ok("CategoryId:" + category.getId());//Dto next step
            return ResponseEntity.ok(subCategoryDtoMapper.toCategoryDto(category));
        }else {
            throw new RuntimeException("La catégorie '" + category.getTitle() + "' existe déjà.") ;
        }
    }

    public List<CategoryDto> rechercher(){
        List<Category> categoryList = this.categoryRepository.findAll();
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category : categoryList){
            categoryDtoList.add(subCategoryDtoMapper.toCategoryDto(category));
        }
        return categoryDtoList;
    }

    public Category lire(Long id) {
        Optional<Category> optionalCategory =  this.categoryRepository.findById(id);
        return optionalCategory.orElseThrow(() -> new EntityNotFoundException("Aucune categorie n'existe avec cette id"));
    }

    // Update
    public Category updateCategory(Long id, Category categoryDetails) {
        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        categoryDetails.setUpdateBy(userConnected);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));

        category.setUpdateBy(categoryDetails.getUpdateBy());
        category.setTitle(categoryDetails.getTitle());
        category.setSlug(categoryDetails.getSlug());
        category.setDeletedAt(categoryDetails.getDeletedAt());
        category.setDeletedBy(categoryDetails.getDeletedBy());

        return categoryRepository.save(category);
    }

    // Delete
    public void deleteCategory(Long id, Category categoryDetails) {
        User userConnected = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        categoryDetails.setDeletedBy(userConnected);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));

        categoryRepository.delete(category);
    }
}