package splat.core;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.ApplicationContainer.ContainerState;

@Slf4j
@RequiredArgsConstructor
@Component
public class SplatPlatform implements Platform {
	
	@NonNull
	private final ApplicationRepository repository;
	@NonNull
	private final RuntimeService runtimeService;

	@Override
	public void createNewApplicationFromArtifact(ApplicationArtifact applicationArtifact) throws PlatformException {
		try {
			Application application = Application.builder().name(applicationArtifact.getName()).artifact(applicationArtifact).build();
			Application createdApplication = repository.save(application);
			runtimeService.deploy(createdApplication);
		} catch (Exception e) {
			throw new PlatformException(e);
		}
	}

	@Override
	public void delete(String appName) throws PlatformException {
		try {
		//	runtimeService.delete(appName);
			repository.removeByName(appName);
		} catch (Exception e) {
			throw new PlatformException(e);
		}
	}
	
	@Override
	public void restart(String appName) throws PlatformException {
		try {
			Application application = repository.findByName(appName);
		//	runtimeService.restart(application);
		} catch (Exception e) {
			throw new PlatformException(e);
		}
		
	}
	
	@Override
	public void stop(String appName) throws PlatformException {
		try {
			Application application = repository.findByName(appName);
	//		runtimeService.stop(application);
		} catch (Exception e) {
			throw new PlatformException(e);
		}
	}

	@Override
	public Set<PlatformApplication> getAllApplications() {
		return repository.findAll().map(this::toPlatformApplication).collect(Collectors.toSet());
	}

	private PlatformApplication toPlatformApplication(Application app) {
		ApplicationContainer container = ApplicationContainer.builder().status(ContainerState.UNKNOWN).build(); // runtimeService.getContainer(app.getName());
		return PlatformApplication.builder().application(app).container(container)
				.build();
	}

}
