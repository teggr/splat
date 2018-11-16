package splat.os.processes;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.Processes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.ApplicationConfiguration;
import splat.core.ApplicationContainer;
import splat.core.ApplicationContainer.ApplicationContainerBuilder;
import splat.core.ApplicationContainer.ContainerState;
import splat.core.ApplicationContainerService;
import splat.core.SpringBootApplicationProperties;
import splat.core.Starter;
import splat.os.fs.FileSystemEnvironment;
import splat.os.ports.Ports;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProcessApplicationContainerService implements ApplicationContainerService, InitializingBean {

	@NonNull
	private final FileSystemEnvironment environment;
	@NonNull
	private final Ports ports;

	private static final FileFilter applicationConfigurationDirectoryFilter = FileFilterUtils.directoryFileFilter();

	private File runtimeDirectory;

	@Override
	public void afterPropertiesSet() throws Exception {

		log.info("Initialising Application Container directory");

		runtimeDirectory = new File(environment.getHomeDirectory(), "runtime");
		if (!runtimeDirectory.exists() && !runtimeDirectory.mkdirs()) {
			throw new IOException("Could not create runtime directory " + runtimeDirectory + " with parent directorys");
		}

		getApplicationConfigurationDirectories().map(this::readContainer)
				.forEach(c -> ports.allocate(c.getServerPort()));

	}

	@Override
	public ApplicationContainer get(String applicationId) {
		return getApplicationConfigurationDirectories().filter(directoryNameIs(applicationId)).findFirst()
				.map(this::readContainer).orElse(ApplicationContainer.empty());
	}

	@Override
	public ApplicationContainer start(ApplicationConfiguration configuration) {

		String applicationId = configuration.getApplicationId();

		log.info("Creating application container {}", applicationId);

		ApplicationContainerBuilder builder = ApplicationContainer.builder().name(applicationId)
				.status(ContainerState.UNKNOWN);

		File containerDirectory = new File(runtimeDirectory, applicationId);
		if (!containerDirectory.exists() && !containerDirectory.mkdirs()) {
			String msg = "Could not create runtime directory " + runtimeDirectory + " with parent directorys";
			log.error(msg);
			throw new RuntimeException(msg);
		}

		log.info("Created application container directory {}", containerDirectory);

		String jarFileName = applicationId + ".jar";
		File runtimeArtifact = new File(containerDirectory, jarFileName);
		try {
			FileUtils.copyInputStreamToFile(configuration.getArtifact().getInputStream(), runtimeArtifact);
		} catch (Exception e) {
			String msg = "Could not copy artifact to " + runtimeDirectory;
			log.error(msg);
			throw new RuntimeException(msg);
		}

		File configDirectory = new File(containerDirectory, "config");
		if (!configDirectory.exists() && !configDirectory.mkdirs()) {
			String msg = "Could not create runtime configuration directory " + configDirectory;
			log.error(msg);
			throw new RuntimeException(msg);
		}

		File applicationProperties = new File(configDirectory, "application.properties");
		try {
			if (!applicationProperties.exists() && !applicationProperties.createNewFile()) {
				throw new RuntimeException("Could not create application properties " + applicationProperties);
			}
			Properties properties = new Properties();
			properties.setProperty(SpringBootApplicationProperties.SERVER_PORT, String.valueOf(ports.allocate()));
			properties.setProperty(SpringBootApplicationProperties.LOGGING_PATH, containerDirectory.getAbsolutePath());
			properties.setProperty(SpringBootApplicationProperties.SERVER_SERVLET_CONTEXT_PATH, "/" + applicationId);
			try (FileWriter writer = new FileWriter(applicationProperties)) {
				properties.store(writer, "Splat Properties for " + applicationId);
			}
			builder.properties(properties);
		} catch (Exception e) {
			String msg = "Could not create runtime configuration properties " + applicationProperties;
			log.error(msg);
			throw new RuntimeException(msg);
		}

		String[] execCommand = new String[] { "-jar", jarFileName };
		File runFile = new File(containerDirectory, applicationId + ".run");
		try {
			if (!runFile.exists() && !runFile.createNewFile()) {
				throw new RuntimeException("Could not create application properties " + applicationProperties);
			}
			FileUtils.writeStringToFile(runFile, StringUtils.join(execCommand, ","), Charset.defaultCharset());
		} catch (Exception e) {
			String msg = "Could not create runtime args " + applicationProperties;
			log.error(msg);
			throw new RuntimeException(msg);
		}

		builder.starter(new MyStarter(applicationId, containerDirectory, execCommand));

		log.info("Finished creating application container {}", applicationId);

		return startContainer(builder.build());
	}

	@Override
	public ApplicationContainer stop(String applicationId) {

		log.info("Stopping Application Container " + applicationId);

		ApplicationContainer container = getApplicationConfigurationDirectories().filter(directoryNameIs(applicationId))
				.findFirst().map(this::readContainer).orElse(null);
		if (container == null) {
			return ApplicationContainer.empty();
		}

		if (!container.isAlive()) {
			return container;
		}

		try {
			container.getProcess().destroyForcefully().waitFor();
			log.info("Stopped application {}", applicationId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ApplicationContainer.from(container).status(ContainerState.STOPPED).build();

	}

	@Override
	public ApplicationContainer delete(String applicationId) {

		log.info("Deleting Application Container " + applicationId);

		ApplicationContainer container = getApplicationConfigurationDirectories().filter(directoryNameIs(applicationId))
				.findFirst().map(this::readContainer).orElse(null);
		if (container == null) {
			return ApplicationContainer.empty();
		}

		ports.deallocate(container.getServerPort());

		try {
			File applicationFolder = new File(runtimeDirectory, applicationId);
			FileUtils.deleteDirectory(applicationFolder);
		} catch (IOException e) {
			log.error("{}", e.getMessage(), e);
		}

		return ApplicationContainer.builder().name(applicationId).status(ContainerState.UNKNOWN)
				.properties(new Properties()).starter(new Starter() {

					@Override
					public void start() throws Exception {

					}
				}).build();

	}

	@Override
	public ApplicationContainer restart(String applicationId) {

		log.info("Restarting Application Container " + applicationId);

		ApplicationContainer applicationContainer = stop(applicationId);
		return startContainer(applicationContainer);

	}

	private Stream<File> getApplicationConfigurationDirectories() {
		return Stream.of(runtimeDirectory.listFiles(applicationConfigurationDirectoryFilter));
	}

	private ApplicationContainer readContainer(File applicationDirectory) {

		String applicationId = FilenameUtils.getBaseName(applicationDirectory.getName());

		// gather pid
		ContainerState state = ContainerState.UNKNOWN;
		PidProcess newPidProcess = null;
		File pidFile = new File(applicationDirectory, applicationId + ".pid");
		if (pidFile.exists()) {
			try {
				newPidProcess = Processes
						.newPidProcess(Integer.parseInt(FileUtils.readFileToString(pidFile, Charset.defaultCharset())));
				state = ContainerState.RUNNING;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		// gather run args
		String[] execCommand = new String[0];
		File runFile = new File(applicationDirectory, applicationId + ".run");
		if (runFile.exists()) {
			try {
				execCommand = StringUtils.split(FileUtils.readFileToString(runFile, Charset.defaultCharset()), ",");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		// gather app properties
		File configurationDirectory = new File(applicationDirectory, "config");
		File applicationPropertiesFile = new File(configurationDirectory, "application.properties");
		Properties applicationProperties = new Properties();
		try (FileInputStream fileInputStream = new FileInputStream(applicationPropertiesFile)) {
			applicationProperties.load(fileInputStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return ApplicationContainer.builder().name(applicationId).process(newPidProcess).status(state)
				.properties(applicationProperties)
				.starter(new MyStarter(applicationId, applicationDirectory, execCommand)).build();

	}

	private static class MyStarter implements Starter {

		private final File applicationDirectory;
		private final String[] execCommand;
		private final String applicationId;

		public MyStarter(String applicationId, File applicationDirectory, String[] execCommand) {
			this.applicationId = applicationId;
			this.applicationDirectory = applicationDirectory;
			this.execCommand = execCommand;
		}

		@Override
		public void start() throws Exception {

			ProcessBuilder processBuilder = new JavaExecutable().processBuilder(execCommand)
					.directory(applicationDirectory);

			PidProcess pidProcess = Processes.newPidProcess(processBuilder.start());

			// create a pid file?
			File pidFile = new File(applicationDirectory, applicationId + ".pid");
			FileUtils.writeStringToFile(pidFile, String.valueOf(pidProcess.getPid()), Charset.defaultCharset());

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

	public ApplicationContainer startContainer(ApplicationContainer container) {

		try {

			log.info("Starting container {}", container.getName());

			container.start();
			return container;

		} catch (Exception e) {
			String msg = "Could not start application container " + container.getName();
			log.error(msg, e);
			throw new RuntimeException(msg);
		}

	}

}
