package will.dev.BTBTEST;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling// Permet de programmer les taches
public class BtbtestApplication {

	public static void main(String[] args) {
		SpringApplication.run(BtbtestApplication.class, args);
	}

}
