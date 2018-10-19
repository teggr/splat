package splat;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilesystemStorageService implements StorageService {

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
	public void store(MultipartFile file) throws IOException {
		Files.copy(file.getInputStream(), path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
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
