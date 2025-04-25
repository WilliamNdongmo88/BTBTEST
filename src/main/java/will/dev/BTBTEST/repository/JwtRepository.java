package will.dev.BTBTEST.repository;

import org.springframework.data.repository.CrudRepository;
import will.dev.BTBTEST.entity.Jwt;
import will.dev.BTBTEST.entity.User;

public interface JwtRepository extends CrudRepository<Jwt, Long> {
    Jwt findByUser(User user);
}
