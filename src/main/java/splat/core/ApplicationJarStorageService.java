package splat.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;

public interface ApplicationJarStorageService {

	void init() throws IOException;

	void store(ApplicationJarResource applicationJarResource) throws IOException;

	Stream<Path> loadAll() throws IOException;

	Path load(String filename) throws IOException;

	Resource loadAsResource(String filename) throws IOException;

	void deleteAll() throws IOException;

}
