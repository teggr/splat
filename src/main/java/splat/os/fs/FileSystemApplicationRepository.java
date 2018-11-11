package splat.os.fs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.Application;
import splat.core.ApplicationRepository;

@Slf4j
@RequiredArgsConstructor
@Component
class FileSystemApplicationRepository implements ApplicationRepository, InitializingBean {

	@NonNull
	private final FileSystemEnvironment environment;
	private File applicationsDirectory;

	@Override
	public void afterPropertiesSet() throws Exception {

		log.info("Initialising Application Configuration directory");

		applicationsDirectory = new File(environment.getHomeDirectory(), "applications");
		if (!applicationsDirectory.exists() && !applicationsDirectory.mkdirs()) {
			throw new IOException(
					"Could not create applications directory " + applicationsDirectory + " with parent directorys");
		}
	}

	@Override
	public Stream<Application> findAll() {
		return getDirectories().map(FileSystemApplicationRepository::toApplication);
	}

	private Stream<File> getDirectories() {
		File[] listFiles = applicationsDirectory.listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
		return Stream.of(listFiles);
	}

	@Override
	public Application findByName(String name) {
		return getDirectories().filter(f -> f.getName().equals(name)).findFirst()
				.map(FileSystemApplicationRepository::toApplication).get();
	}

	private static Application toApplication(File directory) {
		String baseName = FilenameUtils.getBaseName(directory.getName());
		return Application.builder().name(baseName)
				.artifact(new FileArtifactAdapter(new File(directory, baseName + ".jar"))).build();
	}

	@Override
	public Application save(Application application) {

		// create a new application folder from artifact name
		File applicationFolder = new File(applicationsDirectory, application.getName());
		try {

			if (!applicationFolder.mkdirs()) {
				throw new IOException("Could not create application folder " + applicationFolder);
			}

			File artifactFile = new File(applicationFolder, application.getArtifact().getName() + ".jar");

			FileUtils.copyInputStreamToFile(application.getArtifact().getInputStream(), artifactFile);
			
			log.info("Created application artifact {} of size {}", artifactFile.getAbsolutePath(), artifactFile.length());

			return Application.builder().name(application.getName()).artifact(new FileArtifactAdapter(artifactFile))
					.build();

		} catch (IOException e) {
			String message = "Could not save the application " + applicationFolder.getAbsolutePath() + ": "
					+ e.getMessage();
			log.error(message, e);
			throw new FileSystemException(message, e);
		}

	}

	@Override
	public void removeByName(String name) {

		File applicationFolder = new File(applicationsDirectory, name);
		try {
			FileUtils.deleteDirectory(applicationFolder);
			log.info("Deleted directory {}", applicationFolder.getAbsolutePath());
		} catch (IOException e) {
			String message = "Could not delete the application " + applicationFolder.getAbsolutePath() + ": "
					+ e.getMessage();
			log.error(message, e);
			throw new FileSystemException(message, e);
		}

	}

}
