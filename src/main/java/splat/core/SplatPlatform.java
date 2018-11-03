package splat.core;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SplatPlatform implements Platform {

	@NonNull
	private final SplatEnvironment environment;
	@NonNull
	private final ApplicationService applicationService;
	@NonNull
	private final RuntimeService runtimeService;

	@Override
	public void init() throws PlatformException {

		try {

			environment.init();

		} catch (IOException e) {
			throw new PlatformException("Could not initialise environment", e);
		}

		try {
			// initialise services
			applicationService.init();
			runtimeService.init();
		} catch (IOException e) {
			throw new PlatformException("Could not initialise services", e);
		}

	}

	@Override
	public void createNew(ApplicationArtifact applicationArtifact) throws PlatformException {
		try {
			Application application = applicationService.create(applicationArtifact);
			runtimeService.deploy(application);
		} catch (ApplicationServiceException e) {
			throw new PlatformException(e);
		}
	}

	@Override
	public void delete(String appName) throws PlatformException {
		try {
			runtimeService.delete(appName);
			applicationService.delete(appName);
		} catch (ApplicationServiceException e) {
			throw new PlatformException(e);
		}
	}

	@Override
	public Set<PlatformApplication> getAllApplications() {
		return applicationService.findAll().stream().map(this::toPlatformApplication).collect(Collectors.toSet());
	}

	private PlatformApplication toPlatformApplication(Application app) {
		return PlatformApplication.builder().application(app).container(runtimeService.getContainer(app.getName()))
				.build();
	}

}
