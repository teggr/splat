package splat.os.processes;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.Processes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.ApplicationConfiguration;
import splat.core.ApplicationContainer;
import splat.core.ApplicationContainer.ContainerState;
import splat.core.ApplicationContainerService;
import splat.os.fs.FileSystemEnvironment;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProcessApplicationContainerService implements ApplicationContainerService, InitializingBean {

	@NonNull
	private final FileSystemEnvironment environment;

	private static final FileFilter applicationConfigurationDirectoryFilter = FileFilterUtils.directoryFileFilter();

	private File runtimeDirectory;

	@Override
	public void afterPropertiesSet() throws Exception {

		log.info("Initialising Application Container directory");

		runtimeDirectory = new File(environment.getHomeDirectory(), "runtime");
		if (!runtimeDirectory.exists() && !runtimeDirectory.mkdirs()) {
			throw new IOException("Could not create runtime directory " + runtimeDirectory + " with parent directorys");
		}

	}

	@Override
	public ApplicationContainer get(String applicationId) {
		return getApplicationConfigurationDirectories().filter(directoryNameIs(applicationId)).findFirst()
				.map(this::readContainer).orElse(ApplicationContainer.empty());
	}

	@Override
	public ApplicationContainer start(ApplicationConfiguration configuration) {
		return ApplicationContainer.empty();
	}

	@Override
	public ApplicationContainer stop(String applicationId) {
		return ApplicationContainer.empty();
	}

	@Override
	public ApplicationContainer delete(String applicationId) {
		return ApplicationContainer.empty();
	}

	@Override
	public ApplicationContainer restart(String applicationId) {
		return ApplicationContainer.empty();
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
				.properties(applicationProperties).build();

	}

	private static ApplicationContainer toApplication(File directory) {

		return ApplicationContainer.empty();
	}

	private Predicate<File> directoryNameIs(String id) {
		return new Predicate<File>() {
			@Override
			public boolean test(File directory) {
				return FilenameUtils.getBaseName(directory.getName()).equalsIgnoreCase(id);
			}
		};
	}

//	@Override
//	public void deploy(ApplicationConfiguration application) {
////		runtimeScheduler.scheduleTask(new CreateApplicationContainer(application));
//	}
//
//	@Override
//	public void delete(String appName) {
////
////		try {
////			File applicationFolder = new File(runtimeDirectory, appName);
////			FileUtils.deleteDirectory(applicationFolder);
////		} catch (IOException e) {
////			log.error("{}", e.getMessage(), e);
////		}
//
//	}
//
//	@Override
//	public void restart(ApplicationConfiguration application) {
////		runtimeScheduler.scheduleTask(new RestartApplicationContainer(application));
//	}
//
//	@Override
//	public void stop(ApplicationConfiguration find) {
////		ApplicationContainer container = getContainer(find.getName());
////		if (container != null) {
////			if (container.isAlive()) {
////				try {
////					container.getProcess().destroyForcefully().waitFor();
////					log.info("Stopped application {}", find.getName());
////				} catch (Exception e) {
////					e.printStackTrace();
////				}
////
////				ApplicationContainer applicationContainer = ApplicationContainer.builder().name(find.getName())
////						.status(ContainerState.STOPPED).build();
////				containersByName.put(find.getName(), applicationContainer);
////
////			}
////		}
//
//	}
//
//	@RequiredArgsConstructor
//	private class RestartApplicationContainer implements ProcessTask {
//
//		private final ApplicationConfiguration application;
//
//		@Override
//		public String getName() {
//			return "Restart Application container";
//		}
//
//		@Override
//		public void run() {;
//
//			log.info("Restarting application container {}", application.getName());
////
////			ApplicationContainerBuilder builder = ApplicationContainer.builder().name(application.getName());
////
////			File containerDirectory = new File(runtimeDirectory, application.getName());
////			
////			stop(application);
////
////			startProcess(application, builder, containerDirectory);
//
//		}
//
//	}
//
//	@RequiredArgsConstructor
//	private class CreateApplicationContainer implements ProcessTask {
//
//		private final ApplicationConfiguration application;
//
//		@Override
//		public String getName() {
//			return "Create Application container";
//		}
//
//		@Override
//		public void run() {
//
//			log.info("Creating application container {}", application.getName());
//
////			ApplicationContainerBuilder builder = ApplicationContainer.builder().name(application.getName());
////
////			File containerDirectory = new File(runtimeDirectory, application.getName());
////			if (!containerDirectory.exists() && !containerDirectory.mkdirs()) {
////				String msg = "Could not create runtime directory " + runtimeDirectory + " with parent directorys";
////				log.error(msg);
////				failed(builder.status(ContainerState.DEPLOY_FAILED).build());
////				throw new RuntimeException(msg);
////			}
////
////			log.info("Created application container directory {}", containerDirectory);
////
////			File runtimeArtifact = new File(containerDirectory, application.getArtifact().getName());
////			try {
////				FileUtils.copyInputStreamToFile(application.getArtifact().getInputStream(), runtimeArtifact);
////			} catch (Exception e) {
////				String msg = "Could not copy artifact to " + runtimeDirectory;
////				log.error(msg);
////				failed(builder.status(ContainerState.DEPLOY_FAILED).build());
////				throw new RuntimeException(msg);
////			}
////
////			File configDirectory = new File(containerDirectory, "config");
////			if (!configDirectory.exists() && !configDirectory.mkdirs()) {
////				String msg = "Could not create runtime configuration directory " + configDirectory;
////				log.error(msg);
////				failed(builder.status(ContainerState.DEPLOY_FAILED).build());
////				throw new RuntimeException(msg);
////			}
////
////			File applicationProperties = new File(configDirectory, "application.properties");
////			try {
////				if (!applicationProperties.exists() && !applicationProperties.createNewFile()) {
////					throw new RuntimeException("Could not create application properties " + applicationProperties);
////				}
////				Properties properties = new Properties();
////				properties.setProperty("server.port", "8081");
////				properties.setProperty("logging.path", containerDirectory.getAbsolutePath());
////				try (FileWriter writer = new FileWriter(applicationProperties)) {
////					properties.store(writer, "Splat Properties for " + application.getName());
////				}
////
////			} catch (Exception e) {
////				String msg = "Could not create runtime configuration properties " + applicationProperties;
////				log.error(msg);
////				failed(builder.status(ContainerState.DEPLOY_FAILED).build());
////			}
////
////			log.info("Finished creating application container {}", application.getName());
////
////			startProcess(application, builder, containerDirectory);
//
//		}
//
//	}
//
//	private void started(ApplicationContainer container) {
////		containersByName.put(container.getName(), container);
//	}
//
//	private void failed(ApplicationContainer container) {
////		containersByName.put(container.getName(), container);
//	}
//
//	private void startProcess(ApplicationConfiguration application, ApplicationContainerBuilder builder,
//			File containerDirectory) {
//
//		String[] execCommand = new String[] { "-jar", application.getArtifact().getName() };
//
//		log.info("Starting application {}", Arrays.asList(execCommand));
//
////		try {
////
////			ProcessBuilder processBuilder = new JavaExecutable().processBuilder(execCommand)
////					.directory(containerDirectory);
////
////			PidProcess pidProcess = Processes.newPidProcess(processBuilder.start());
////
////			// create a pid file?
////			File pidFile = new File(containerDirectory, application.getName() + ".pid");
////			FileUtils.writeStringToFile(pidFile, String.valueOf(pidProcess.getPid()), Charset.defaultCharset());
////
////			started(builder.status(ContainerState.RUNNING).process(pidProcess).build());
////
////		} catch (Exception e) {
////			String msg = "Could not start application container " + execCommand;
////			log.error(msg, e);
////			failed(builder.status(ContainerState.RUN_FAILED).build());
////			throw new RuntimeException(msg);
////		}
//	}

}
