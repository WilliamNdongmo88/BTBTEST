package will.dev.BTBTEST.repository;

import org.springframework.data.repository.CrudRepository;
import will.dev.BTBTEST.entity.Jwt;

public interface JwtRepository extends CrudRepository<Jwt, Long> {

}
