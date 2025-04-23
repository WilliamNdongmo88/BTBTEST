package will.dev.BTBTEST.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import will.dev.BTBTEST.entity.Jwt;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends CrudRepository<Jwt, Long> {

    @Query("SELECT j From Jwt j WHERE j.user.email =:email AND j.expire = :expire AND j.desactive = :desactive")
    Optional<Jwt> findUserValidToken(String email, Boolean desactive, Boolean expire);

    Optional<Jwt> findByValeurAndDesactiveAndExpire(String valeur, Boolean desactive, Boolean expire);

    @Query("SELECT j From Jwt j WHERE j.user.email =:email")
    Stream<Jwt> findUser(String email);

    @Query("SELECT j FROM Jwt j WHERE j.expire=:expire AND j.desactive=:desactive")
    List<Jwt> deleteAllByExpireAndDesactiveJwt(Boolean expire, Boolean desactive);

    @Query("SELECT j FROM Jwt j WHERE j.refreshToken.valeur=:valeur")
    Optional<Jwt> findByRefreshToken(String valeur);
}
