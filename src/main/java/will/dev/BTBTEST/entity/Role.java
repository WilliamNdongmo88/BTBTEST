package will.dev.BTBTEST.entity;

import jakarta.persistence.*;
import lombok.*;
import will.dev.BTBTEST.enums.TypeDeRole;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeDeRole libelle;
}
