package splat.core;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

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

			File configDirectory = new File(containerDirectory, "configuration");
			if (!configDirectory.exists() && !configDirectory.mkdirs()) {
				String msg = "Could not create runtime configuration directory " + configDirectory;
				log.error(msg);
				applicationContainerCallback.failed(builder.status(ContainerState.DEPLOY_FAILED).build());
				throw new RuntimeException(msg);
			}

			File applicationProperties = new File(configDirectory, "applicationProperties");
			try {
				if (!applicationProperties.exists() && !applicationProperties.createNewFile()) {
					throw new RuntimeException("Could not create application properties " + applicationProperties);
				}
			} catch (Exception e) {
				String msg = "Could not create runtime configuration properties " + applicationProperties;
				log.error(msg);
				applicationContainerCallback.failed(builder.status(ContainerState.DEPLOY_FAILED).build());
			}

			File runtimeArtifact = new File(containerDirectory, application.getArtifact().getName());
			try {
				FileUtils.copyInputStreamToFile(application.getArtifact().getInputStream(), runtimeArtifact);
			} catch (Exception e) {
				String msg = "Could not copy artifact to " + runtimeDirectory;
				log.error(msg);
				applicationContainerCallback.failed(builder.status(ContainerState.DEPLOY_FAILED).build());
				throw new RuntimeException(msg);
			}

			log.info("Finished creating application container {}", application.getName());

			startProcess(application, builder, containerDirectory, applicationContainerCallback);

		}

	}

	private static void startProcess(Application application, ApplicationContainerBuilder builder,
			File containerDirectory, ApplicationContainerCallback applicationContainerCallback) {

		String[] execCommand = new String[] { "-jar", application.getArtifact().getName() };

		// String[] osCommand = new String[] { "CMD", "/C" };

		// String[] commands = ArrayUtils.addAll(osCommand, execCommand);

		log.info("Starting application {}", execCommand);

		try {
			// ProcessBuilder processBuilder = new
			// ProcessBuilder(execCommand).directory(containerDirectory).inheritIO();
			
			ProcessBuilder processBuilder = new JavaExecutable().processBuilder(execCommand).directory(containerDirectory);

			// ProcessBuilder processBuilder = new ProcessBuilder(commands).directory(containerDirectory);

			Process process = processBuilder.start();

			applicationContainerCallback.started(builder.status(ContainerState.RUNNING).process(process).build());

		} catch (Exception e) {
			String msg = "Could not start application container " + execCommand;
			log.error(msg, e);
			applicationContainerCallback.failed(builder.status(ContainerState.RUN_FAILED).build());
			throw new RuntimeException(msg);
		}
	}

}
