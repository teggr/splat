package splat.core;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.ApplicationContainer.ApplicationContainerBuilder;

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
				applicationContainerCallback.failed(builder.status("Failed to deploy").build());
				throw new RuntimeException(msg);
			}

			File runtimeArtifact = new File(containerDirectory, application.getArtifact().getName());
			try {
				FileUtils.copyInputStreamToFile(application.getArtifact().getInputStream(), runtimeArtifact);				
			} catch (Exception e) {
				String msg = "Could not copy artifact to " + runtimeDirectory;
				log.error(msg);
				applicationContainerCallback.failed(builder.status("Failed to deploy").build());
				throw new RuntimeException(msg);
			}
			
			log.info("Finished creating application container {}", application.getName());
			
			String execCommand = String.format("java -jar %s", application.getArtifact().getName());

			log.info("Starting application {}", execCommand);

			try {
				Process process = new ProcessBuilder(execCommand).directory(runtimeDirectory).start();
				
				applicationContainerCallback.started(builder.status("started").build());
				
			} catch (Exception e) {
				String msg = "Could not start application container " + execCommand;
				log.error(msg);
				applicationContainerCallback.failed(builder.status("Failed to start").build());
				throw new RuntimeException(msg);
			}

		}

	}

}
