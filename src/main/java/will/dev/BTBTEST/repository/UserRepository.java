package will.dev.BTBTEST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import will.dev.BTBTEST.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
