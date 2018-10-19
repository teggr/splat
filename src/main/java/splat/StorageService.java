package splat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	void init() throws IOException;

	void store(MultipartFile file) throws IOException;

	Stream<Path> loadAll() throws IOException;

	Path load(String filename) throws IOException;

	Resource loadAsResource(String filename) throws IOException;

	void deleteAll() throws IOException;

}
