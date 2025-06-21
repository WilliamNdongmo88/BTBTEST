package will.dev.BTBTEST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import will.dev.BTBTEST.entity.Files;

public interface FilesRepository extends JpaRepository<Files, Long> {
}
