package will.dev.BTBTEST.dtoMapper;

import org.springframework.stereotype.Component;
import will.dev.BTBTEST.dto.CategoryDto;
import will.dev.BTBTEST.dto.RoleDTO;
import will.dev.BTBTEST.dto.SubCategoryDto;
import will.dev.BTBTEST.dto.UserDto;
import will.dev.BTBTEST.entity.Category;
import will.dev.BTBTEST.entity.Role;
import will.dev.BTBTEST.entity.SubCategory;
import will.dev.BTBTEST.entity.User;

@Component
public class SubCategoryDtoMapper {

    public SubCategoryDto toSubCategoryDto(SubCategory entity) {
        if (entity == null) return null;

        SubCategoryDto dto = new SubCategoryDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setSlug(entity.getSlug());
        dto.setType(entity.getType());
        dto.setCategory(toCategoryDto(entity.getCategory()));
        dto.setAddedBy(toUserDto(entity.getAddedBy()));

        return dto;
    }

    public CategoryDto toCategoryDto(Category category) {
        if (category == null) return null;

        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setTitle(category.getTitle());
        dto.setSlug(category.getSlug());
        dto.setAddedBy(toUserDto(category.getAddedBy()));

        return dto;
    }

    public UserDto toUserDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPassword(user.getPassword());
        dto.setActif(user.getActif());
        dto.setRole(toRoleDto(user.getRole()));

        return dto;
    }

    public RoleDTO toRoleDto(Role role) {
        if (role == null) return null;

        RoleDTO dto = new RoleDTO();
        dto.setLibelle(role.getLibelle()); // libelle est de type `TypeDeRole`
        return dto;
    }
}

