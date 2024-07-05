package naatsms.orchestra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class OrchestraApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrchestraApplication.class, args);
	}

}
