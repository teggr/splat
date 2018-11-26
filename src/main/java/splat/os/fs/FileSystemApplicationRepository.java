package splat.os.fs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

	private XmlMapper xmlMapper = new XmlMapper();

	@Override
	public void afterPropertiesSet() throws Exception {

		log.info("Initialising Application Configuration directory");

		xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);

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

		try {

			// create the configuration file
			File configurationFile = new File(applicationDirectory, "config.xml");

			XmlApplicationConfiguration config = xmlMapper.readValue(configurationFile,
					XmlApplicationConfiguration.class);

			return ApplicationConfiguration.builder().name(config.getName()).applicationId(config.getApplicationId())
					.artifact(
							new FileArtifactAdapter(new File(applicationDirectory, config.getArtifactName() + ".jar")))
					.build();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Predicate<File> directoryNameIs(String id) {
		return new Predicate<File>() {
			@Override
			public boolean test(File directory) {
				return FilenameUtils.getBaseName(directory.getName()).equalsIgnoreCase(id);
			}
		};
	}

	private ApplicationConfiguration writeConfiguration(ApplicationConfiguration applicationConfiguration)
			throws IOException {

		// create a new application folder from artifact name
		File applicationDirectory = new File(applicationsDirectory, applicationConfiguration.getApplicationId());
		if (!applicationDirectory.exists() && !applicationDirectory.mkdirs()) {
			throw new IOException("Could not create application folder " + applicationDirectory);
		}

		// create the configuration file
		XmlApplicationConfiguration configuration = new XmlApplicationConfiguration(applicationConfiguration);
		File configurationFile = new File(applicationDirectory, "config.xml");

		String writeValueAsString = xmlMapper.writer().withRootName("applicationConfiguration")
				.writeValueAsString(configuration);
		FileUtils.writeStringToFile(configurationFile, writeValueAsString, StandardCharsets.UTF_8);

		// store the jar file
		File artifactFile = new File(applicationDirectory, applicationConfiguration.getApplicationId() + ".jar");
		FileUtils.copyInputStreamToFile(applicationConfiguration.getArtifact().getInputStream(), artifactFile);
		log.info("Created application artifact {} of size {}", artifactFile.getAbsolutePath(), artifactFile.length());

		// rebuild the configuraiton object
		return ApplicationConfiguration.builder().name(applicationConfiguration.getName())
				.applicationId(applicationConfiguration.getApplicationId())
				.artifact(new FileArtifactAdapter(artifactFile)).build();

	}

}
