package splat.core;

import java.io.File;

public class SplatRuntimeService implements RuntimeService {

	private final File runtimeDirectory;
	private final RuntimeScheduler runtimeScheduler;

	public SplatRuntimeService(File runtimeDirectory, RuntimeScheduler runtimeScheduler) {
		this.runtimeDirectory = runtimeDirectory;
		this.runtimeScheduler = runtimeScheduler;
	}

	@Override
	public ApplicationContainer getContainer(String name) {
		return ApplicationContainer.builder().name(name).status("loaded").build();
	}

	@Override
	public void deploy(Application application) {
		runtimeScheduler.scheduleApplication(application);
	}

}
