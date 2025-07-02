package will.dev.BTBTEST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import will.dev.BTBTEST.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByTitle(String title);
}

