package splat.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilesystemStorageService implements ApplicationJarStorageService {

	private Path path;

	public FilesystemStorageService(Path path) {
		Objects.requireNonNull(path);
		this.path = path;
	}

	@Override
	public void init() throws IOException {
		log.info("Creating storage directory {}", path);
		Files.createDirectories(path);
	}

	@Override
	public void store(ApplicationJarResource applicationJarResource) throws IOException {
		Files.copy(applicationJarResource.getInputStream(), path.resolve(applicationJarResource.getApplicationName()),
				StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public Stream<Path> loadAll() throws IOException {
		return Files.list(path);
	}

	@Override
	public Path load(String filename) throws IOException {
		return path.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) throws IOException {
		return new PathResource(load(filename));
	}

	@Override
	public void deleteAll() throws IOException {
		loadAll().forEach(p -> {
			try {
				Files.delete(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
