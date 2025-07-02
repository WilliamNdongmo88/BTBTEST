package will.dev.BTBTEST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import will.dev.BTBTEST.entity.SubCategory;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    SubCategory findByTitle(String title);
    List<SubCategory> findByCategoryTitle(String categoryTitle);

    List<SubCategory> findByCategoryId(Long id);
}
