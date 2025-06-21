package will.dev.BTBTEST.dtoMapper;

import org.springframework.context.annotation.Configuration;
import will.dev.BTBTEST.dto.RoleDTO;
import will.dev.BTBTEST.dto.UserDto;
import will.dev.BTBTEST.entity.User;

@Configuration
public class UserDtoMapper {
    // Mapping DTO → Entity
    public User mapToEntity(UserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setActif(dto.getActif());
        return user;
    }

    // Mapping Entity → DTO
    public UserDto mapToDto(User entity) {
        UserDto dto = new UserDto();

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setLibelle(entity.getRole().getLibelle());

        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setName(entity.getName());
        dto.setActif(entity.getActif());
        dto.setRole(roleDTO);
        return dto;
    }
}
