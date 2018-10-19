package splat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SplatApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SplatApplication.class, args);
	}

	@Bean
	@ConfigurationProperties("storage")
	public StorageProperties storageProperties() {
		return new StorageProperties();
	}

	@Bean
	public FilesystemStorageService filesystemStorageService(StorageProperties properties) {
		return new FilesystemStorageService(properties.getPath());
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
			storageService.deleteAll();
		};
	}

}
