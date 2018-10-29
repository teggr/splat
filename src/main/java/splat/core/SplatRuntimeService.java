package splat.core;

import java.io.File;

public class SplatRuntimeService implements RuntimeService {

	private final File runtimeDirectory;

	public SplatRuntimeService(File runtimeDirectory) {
		this.runtimeDirectory = runtimeDirectory;
	}

	@Override
	public ApplicationContainer getContainer(String name) {
		return ApplicationContainer.builder().name(name).status("loaded").build();
	}

}
