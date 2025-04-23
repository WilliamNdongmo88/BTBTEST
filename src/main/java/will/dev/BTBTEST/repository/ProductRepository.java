package will.dev.BTBTEST.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import will.dev.BTBTEST.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.name LIKE CONCAT('%', :param, '%')")
    List<Product> findAllProductByName(String param);

    Optional<Product> findByName(String name);
}
