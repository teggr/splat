package splat.core;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SplatRuntimeService implements RuntimeService {

	private final SplatEnvironment environment;
	private final RuntimeScheduler runtimeScheduler;
	private File runtimeDirectory;

	@Override
	public void init() throws IOException {
		runtimeDirectory = new File(environment.getHomeDirectory(), "runtime");
		if (!runtimeDirectory.exists() && !runtimeDirectory.mkdirs()) {
			throw new IOException("Could not create runtime directory " + runtimeDirectory + " with parent directorys");
		}
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
