package splat.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.springframework.stereotype.Component;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.Processes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.ApplicationContainer.ContainerState;

@Slf4j
@RequiredArgsConstructor
@Component
public class SplatRuntimeService implements RuntimeService {

	private final SplatEnvironment environment;
	private final RuntimeScheduler runtimeScheduler;
	private File runtimeDirectory;
	private final Map<String, ApplicationContainer> containersByName = new HashMap<String, ApplicationContainer>();

	@Override
	public void init() throws IOException {
		runtimeDirectory = new File(environment.getHomeDirectory(), "runtime");
		if (!runtimeDirectory.exists() && !runtimeDirectory.mkdirs()) {
			throw new IOException("Could not create runtime directory " + runtimeDirectory + " with parent directorys");
		}

		// read in all exists app containers
		File[] listFiles = runtimeDirectory.listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
		containersByName.putAll(Stream.of(listFiles).map(SplatRuntimeService::toApplication)
				.collect(Collectors.toMap(ac -> ac.getName(), ac -> ac)));

	}

	private static ApplicationContainer toApplication(File directory) {
		String baseName = FilenameUtils.getBaseName(directory.getName());
		ContainerState state = ContainerState.UNKNOWN;
		PidProcess newPidProcess = null;
		File pidFile = new File(directory, baseName + ".pid");
		if (pidFile.exists()) {
			try {
				newPidProcess = Processes
						.newPidProcess(Integer.parseInt(FileUtils.readFileToString(pidFile, Charset.defaultCharset())));
				state = ContainerState.RUNNING;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ApplicationContainer.builder().name(baseName).status(state).process(newPidProcess).build();
	}

	@Override
	public ApplicationContainer getContainer(String name) {
		if (containersByName.containsKey(name)) {
			return containersByName.get(name);
		} else {
			return ApplicationContainer.builder().name(name).status(ContainerState.UNKNOWN).build();
		}

	}

	@Override
	public void deploy(Application application) {
		runtimeScheduler.scheduleApplication(application, runtimeDirectory, new ApplicationContainerCallback() {
			@Override
			public void started(ApplicationContainer container) {
				containersByName.put(container.getName(), container);
			}

			@Override
			public void failed(ApplicationContainer container) {
				containersByName.put(container.getName(), container);
			}
		});
	}

	@Override
	public void delete(String appName) {

		try {
			File applicationFolder = new File(runtimeDirectory, appName);
			FileUtils.deleteDirectory(applicationFolder);
		} catch (IOException e) {
			log.error("{}", e.getMessage(), e);
		}

	}

	@Override
	public void restart(Application application) {
		runtimeScheduler.restartApplication(application, runtimeDirectory, new ApplicationContainerCallback() {

			@Override
			public void started(ApplicationContainer container) {
				containersByName.put(container.getName(), container);
			}

			@Override
			public void failed(ApplicationContainer container) {
				containersByName.put(container.getName(), container);
			}
		});
	}

	@Override
	public void stop(Application find) {
		ApplicationContainer container = getContainer(find.getName());
		if (container != null) {
			if (container.isAlive()) {
				try {
					container.getProcess().destroyForcefully().waitFor();
					log.info("Stopped application {}", find.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}

				ApplicationContainer applicationContainer = ApplicationContainer.builder().name(find.getName())
						.status(ContainerState.STOPPED).build();
				containersByName.put(find.getName(), applicationContainer);

			}
		}

	}

}
