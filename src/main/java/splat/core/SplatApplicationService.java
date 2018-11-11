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
public class SplatApplicationService implements ApplicationService {

	@NonNull
	private final ApplicationConfigurationRepository repository;
	@NonNull
	private final RuntimeService runtimeService;

	@Override
	public Application createFromArtifact(ApplicationArtifact applicationArtifact) throws ApplicationServiceException {
		try {

			// does the application already exist
			String applicationId = applicationArtifact.getName();
			if (repository.exists(applicationId)) {
				throw new ApplicationServiceException("Application " + applicationId + " already exists");
			}

			ApplicationConfiguration configuration = ApplicationConfiguration.builder().applicationId(applicationId)
					.name(applicationArtifact.getName()).artifact(applicationArtifact).build();

			return builderApplication(repository.save(configuration));

		} catch (Exception e) {
			throw new ApplicationServiceException("Could not create a new application", e);
		}
	}

	@Override
	public void delete(String applicationId) throws ApplicationServiceException {
		try {
			// runtimeService.delete(appName);
			repository.remove(applicationId);
		} catch (Exception e) {
			throw new ApplicationServiceException("Could not delete application " + applicationId, e);
		}
	}

	@Override
	public Application restart(String applicationId) throws ApplicationServiceException {
		try {
			ApplicationConfiguration configuration = repository.find(applicationId);
			// runtimeService.restart(application);
			return builderApplication(configuration);
		} catch (Exception e) {
			throw new ApplicationServiceException("Could not restart application " + applicationId, e);
		}

	}

	@Override
	public Application stop(String applicationId) throws ApplicationServiceException {
		try {
			ApplicationConfiguration configuration = repository.find(applicationId);
			return builderApplication(configuration);
		} catch (Exception e) {
			throw new ApplicationServiceException("Could not stop application " + applicationId, e);
		}
	}

	@Override
	public Set<Application> getAllApplications() {
		return repository.findAll().map(this::builderApplication).collect(Collectors.toSet());
	}

	private Application builderApplication(ApplicationConfiguration configuration) {
		return new Application(configuration, ApplicationContainer.empty());
	}

}
