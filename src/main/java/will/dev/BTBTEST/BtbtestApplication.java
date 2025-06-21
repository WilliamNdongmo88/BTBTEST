package will.dev.BTBTEST;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.SecretKey;
import java.util.Base64;

@SpringBootApplication
@EnableScheduling// Permet de programmer les taches
public class BtbtestApplication {

	public static void main(String[] args) {
		SpringApplication.run(BtbtestApplication.class, args);

//		SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//		String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
//		System.out.println("BASE64 Key: " + base64Key);
	}

}
