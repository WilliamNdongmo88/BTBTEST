package will.dev.BTBTEST.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jwt")
public class Jwt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valeur", columnDefinition = "TEXT")
    private String valeur;

    private Boolean desactive;
    private Boolean expire;

    //-------------Branch refresh token--------------
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})//La suppression de la table Jwt supprimera égamemt la table refreshToken
    private RefreshToken refreshToken;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})//La suppression de la table Jwt ne supprimera pas la table user
    @JoinColumn(name = "user_id")
    private User user;
}
