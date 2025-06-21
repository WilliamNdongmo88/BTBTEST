package will.dev.BTBTEST.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    @NotNull(message = "Email ne peut pas être null")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Nom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Nom entre 2 et 50 caractères")
    private String name;
    private String password;
    private Boolean actif;

    private RoleDTO role;
}
