package will.dev.BTBTEST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import will.dev.BTBTEST.entity.EmailValids;

import java.util.Optional;

public interface EmailValidsRepository extends JpaRepository<EmailValids, Long> {
    @Query("SELECT e FROM EmailValids e WHERE e.email=:email")
    Optional<EmailValids> findByEmail(String email);
}
