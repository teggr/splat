package splat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import splat.core.ApplicationJarStorageService;
import splat.core.FilesystemStorageService;
import splat.core.StorageProperties;

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
	CommandLineRunner init(ApplicationJarStorageService storageService) {
		return (args) -> {
			storageService.init();
			storageService.deleteAll();
		};
	}

}
