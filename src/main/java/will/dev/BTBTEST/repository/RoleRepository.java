package will.dev.BTBTEST.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import will.dev.BTBTEST.enums.TypeDeRole;
import will.dev.BTBTEST.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByLibelle(TypeDeRole typeDeRole);
}
