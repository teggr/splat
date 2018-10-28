package splat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import splat.core.Platform;

@SpringBootApplication
public class SplatApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SplatApplication.class, args);
	}

	@Bean
	CommandLineRunner init(Platform platform) {
		return (args) -> {
			platform.init();
		};
	}

}
