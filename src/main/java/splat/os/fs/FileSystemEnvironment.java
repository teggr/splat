package splat.os.fs;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileSystemEnvironment implements InitializingBean {

	@NonNull
	private final FileSystemProperties properties;
	
	private File homeDirectory;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		log.info("Initialising Home Directory");
	
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
