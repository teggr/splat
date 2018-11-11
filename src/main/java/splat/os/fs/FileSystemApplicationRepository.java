package splat.os.fs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.function.Predicate;
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
import splat.core.ApplicationConfiguration;
import splat.core.ApplicationConfigurationRepository;

@Slf4j
@RequiredArgsConstructor
@Component
class FileSystemApplicationRepository implements ApplicationConfigurationRepository, InitializingBean {

	@NonNull
	private final FileSystemEnvironment environment;

	private static final FileFilter applicationConfigurationDirectoryFilter = FileFilterUtils.directoryFileFilter();

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
	public Stream<ApplicationConfiguration> findAll() {
		return getApplicationConfigurationDirectories().map(this::readConfiguration);
	}

	@Override
	public ApplicationConfiguration find(String id) {
		return getApplicationConfigurationDirectories().filter(directoryNameIs(id)).findFirst()
				.map(this::readConfiguration)
				.orElseThrow(() -> new FileSystemException("Could not find application configuration " + id));
	}

	@Override
	public boolean exists(String id) {
		return getApplicationConfigurationDirectories().anyMatch(directoryNameIs(id));
	}

	@Override
	public ApplicationConfiguration save(ApplicationConfiguration applicationConfiguration) {

		try {

			return writeConfiguration(applicationConfiguration);

		} catch (IOException e) {
			String message = "Could not save the application " + applicationConfiguration.getName() + ": "
					+ e.getMessage();
			log.error(message, e);
			throw new FileSystemException(message, e);
		}

	}

	@Override
	public void remove(String id) {

		File applicationFolder = new File(applicationsDirectory, id);
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

	private Stream<File> getApplicationConfigurationDirectories() {
		return Stream.of(applicationsDirectory.listFiles(applicationConfigurationDirectoryFilter));
	}

	private ApplicationConfiguration readConfiguration(File applicationDirectory) {
		String applicationId = FilenameUtils.getBaseName(applicationDirectory.getName());
		return ApplicationConfiguration.builder().name(applicationId).applicationId(applicationId)
				.artifact(new FileArtifactAdapter(new File(applicationDirectory, applicationId + ".jar"))).build();
	}

	private Predicate<File> directoryNameIs(String id) {
		return new Predicate<File>() {
			public boolean test(File directory) {
				return FilenameUtils.getBaseName(directory.getName()).equalsIgnoreCase(id);
			}
		};
	}

	private ApplicationConfiguration writeConfiguration(ApplicationConfiguration applicationConfiguration)
			throws IOException {

		// create a new application folder from artifact name
		File applicationFolder = new File(applicationsDirectory, applicationConfiguration.getApplicationId());
		if (!applicationFolder.exists() && !applicationFolder.mkdirs()) {
			throw new IOException("Could not create application folder " + applicationFolder);
		}

		File artifactFile = new File(applicationFolder, applicationConfiguration.getArtifact().getName() + ".jar");

		FileUtils.copyInputStreamToFile(applicationConfiguration.getArtifact().getInputStream(), artifactFile);

		log.info("Created application artifact {} of size {}", artifactFile.getAbsolutePath(), artifactFile.length());

		return ApplicationConfiguration.builder().name(applicationConfiguration.getName())
				.applicationId(applicationConfiguration.getApplicationId())
				.artifact(new FileArtifactAdapter(artifactFile)).build();

	}

}
