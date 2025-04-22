package will.dev.BTBTEST.repository;

import org.springframework.data.repository.CrudRepository;
import will.dev.BTBTEST.entity.User;
import will.dev.BTBTEST.entity.Validation;

import java.util.Optional;

public interface ValidationRepository extends CrudRepository<Validation, Long> {

    Optional<Validation> findByCode(String code);

    Optional<Validation> findByUser(User user);
}
