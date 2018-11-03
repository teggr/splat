package splat.core;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SplatEnvironment {

	@NonNull
	private final SplatProperties properties;
	
	private File homeDirectory;
	
	public void init() throws IOException {
		homeDirectory = properties.getHomeDirectory();
		if (homeDirectory == null) {
			homeDirectory = new File(System.getProperty("user.home"), "/.splat");
		}

		log.info("Using home directory {}", homeDirectory);

		if (!homeDirectory.exists() && !homeDirectory.mkdirs()) {
			throw new IOException("Could not create home directory " + homeDirectory + " with parent directorys");
		}

		if (!homeDirectory.exists()) {
			throw new IOException("Home directory " + homeDirectory + " does not exist");
		}
	}
	
	public File getHomeDirectory() {
		return homeDirectory;
	}
	
}
