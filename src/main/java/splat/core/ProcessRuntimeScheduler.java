package splat.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.ProcessIdUtil;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.zeroturnaround.process.JavaProcess;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.Processes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.ApplicationContainer.ApplicationContainerBuilder;
import splat.core.ApplicationContainer.ContainerState;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProcessRuntimeScheduler implements RuntimeScheduler {

	@NonNull
	private final TaskExecutor executor;

	@Override
	public void scheduleApplication(Application application, File runtimeDirectory,
			ApplicationContainerCallback applicationContainerCallback) {
		log.info("Scheduling {}", application.getName());
		executor.execute(new CreateApplicationContainer(application, runtimeDirectory, applicationContainerCallback));
	}

	@Override
	public void restartApplication(Application application, File runtimeDirectory,
			ApplicationContainerCallback applicationContainerCallback) {
		log.info("Restarting {}", application);
		executor.execute(new RestartApplicationContainer(application, runtimeDirectory, applicationContainerCallback));
	}

	@RequiredArgsConstructor
	private static class RestartApplicationContainer implements Runnable {

		private final Application application;
		private final File runtimeDirectory;
		private final ApplicationContainerCallback applicationContainerCallback;

		@Override
		public void run() {

			log.info("Restarting application container {}", application.getName());

			ApplicationContainerBuilder builder = ApplicationContainer.builder().name(application.getName());

			File containerDirectory = new File(runtimeDirectory, application.getName());

			startProcess(application, builder, containerDirectory, applicationContainerCallback);

		}

	}

	@RequiredArgsConstructor
	private static class CreateApplicationContainer implements Runnable {

		private final Application application;
		private final File runtimeDirectory;
		private final ApplicationContainerCallback applicationContainerCallback;

		@Override
		public void run() {

			log.info("Creating application container {}", application.getName());

			ApplicationContainerBuilder builder = ApplicationContainer.builder().name(application.getName());

			File containerDirectory = new File(runtimeDirectory, application.getName());
			if (!containerDirectory.exists() && !containerDirectory.mkdirs()) {
				String msg = "Could not create runtime directory " + runtimeDirectory + " with parent directorys";
				log.error(msg);
				applicationContainerCallback.failed(builder.status(ContainerState.DEPLOY_FAILED).build());
				throw new RuntimeException(msg);
			}
			
			log.info("Created application container directory {}", containerDirectory);

			File runtimeArtifact = new File(containerDirectory, application.getArtifact().getName());
			try {
				FileUtils.copyInputStreamToFile(application.getArtifact().getInputStream(), runtimeArtifact);
			} catch (Exception e) {
				String msg = "Could not copy artifact to " + runtimeDirectory;
				log.error(msg);
				applicationContainerCallback.failed(builder.status(ContainerState.DEPLOY_FAILED).build());
				throw new RuntimeException(msg);
			}

			File configDirectory = new File(containerDirectory, "config");
			if (!configDirectory.exists() && !configDirectory.mkdirs()) {
				String msg = "Could not create runtime configuration directory " + configDirectory;
				log.error(msg);
				applicationContainerCallback.failed(builder.status(ContainerState.DEPLOY_FAILED).build());
				throw new RuntimeException(msg);
			}

			File applicationProperties = new File(configDirectory, "application.properties");
			try {
				if (!applicationProperties.exists() && !applicationProperties.createNewFile()) {
					throw new RuntimeException("Could not create application properties " + applicationProperties);
				}
				Properties properties = new Properties();
				properties.setProperty("server.port", "8081");
				properties.setProperty("logging.path", containerDirectory.getAbsolutePath());
				try (FileWriter writer = new FileWriter(applicationProperties)) {
					properties.store(writer, "Splat Properties for " + application.getName());
				}

			} catch (Exception e) {
				String msg = "Could not create runtime configuration properties " + applicationProperties;
				log.error(msg);
				applicationContainerCallback.failed(builder.status(ContainerState.DEPLOY_FAILED).build());
			}

			log.info("Finished creating application container {}", application.getName());

			startProcess(application, builder, containerDirectory, applicationContainerCallback);

		}

	}

	private static void startProcess(Application application, ApplicationContainerBuilder builder,
			File containerDirectory, ApplicationContainerCallback applicationContainerCallback) {

		String[] execCommand = new String[] { "-jar", application.getArtifact().getName() };

		log.info("Starting application {}", execCommand);

		try {

			ProcessBuilder processBuilder = new JavaExecutable().processBuilder(execCommand)
					.directory(containerDirectory);

			PidProcess pidProcess = Processes.newPidProcess(processBuilder.start());

			// create a pid file?
			File pidFile = new File(containerDirectory, application.getName() + ".pid");
			FileUtils.writeStringToFile(pidFile, String.valueOf(pidProcess.getPid()), Charset.defaultCharset());

			applicationContainerCallback.started(builder.status(ContainerState.RUNNING).process(pidProcess).build());

		} catch (Exception e) {
			String msg = "Could not start application container " + execCommand;
			log.error(msg, e);
			applicationContainerCallback.failed(builder.status(ContainerState.RUN_FAILED).build());
			throw new RuntimeException(msg);
		}
	}

}
