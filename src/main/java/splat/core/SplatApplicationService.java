package splat.core;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.os.ports.Ports;

@Slf4j
@RequiredArgsConstructor
@Component
public class SplatApplicationService implements ApplicationService, InitializingBean {

	@NonNull
	private final ApplicationConfigurationRepository repository;
	@NonNull
	private final ApplicationContainerService containers;
	@NonNull
	private final ProxyService proxyService;
	@NonNull
	private final LocationService locationService;
	@NonNull
	private final ApplicationContainerProcessor applicationContainerProcessor;
	@NonNull
	private final Ports ports;

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("Processing all the containers");
		containers.getAll().stream().forEach(applicationContainerProcessor::process);
	}

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

			ApplicationConfiguration applicationConfiguration = repository.save(configuration);
			ApplicationContainer container = containers.start(configuration);
			proxyService.start(container.getServerPort(), container.getContextPath());
			return toApplication(applicationConfiguration, container);

		} catch (Exception e) {
			throw new ApplicationServiceException("Could not create a new application", e);
		}
	}

	@Override
	public void delete(String applicationId) throws ApplicationServiceException {
		try {
			ApplicationContainer container = containers.stop(applicationId);
			containers.delete(applicationId);
			proxyService.remove(container.getContextPath());
			repository.remove(applicationId);
		} catch (Exception e) {
			throw new ApplicationServiceException("Could not delete application " + applicationId, e);
		}
	}

	@Override
	public Application restart(String applicationId) throws ApplicationServiceException {
		try {
			ApplicationConfiguration configuration = repository.find(applicationId);
			return toApplication(configuration, containers.restart(applicationId));
		} catch (Exception e) {
			throw new ApplicationServiceException("Could not restart application " + applicationId, e);
		}

	}

	@Override
	public Application stop(String applicationId) throws ApplicationServiceException {
		try {
			ApplicationConfiguration configuration = repository.find(applicationId);
			return toApplication(configuration, containers.stop(applicationId));
		} catch (Exception e) {
			throw new ApplicationServiceException("Could not stop application " + applicationId, e);
		}
	}

	@Override
	public Set<Application> getAllApplications() {
		return repository.findAll().map(this::toApplication).collect(Collectors.toSet());
	}

	private Application toApplication(ApplicationConfiguration configuration) {
		return toApplication(configuration, containers.get(configuration.getApplicationId()));
	}

	private Application toApplication(ApplicationConfiguration configuration, ApplicationContainer container) {
		return new Application(locationService.getLocation(container.getServerPort(), container.getContextPath()),
				configuration, container);
	}

	@Override
	public Application getApplication(String applicationId) throws ApplicationServiceException {
		ApplicationConfiguration configuration = repository.find(applicationId);
		return toApplication(configuration, containers.get(applicationId));
	}

	@Override
	public void fixPorts(String applicationId, int from, int to) throws ApplicationServiceException {
		ApplicationConfiguration configuration = repository.find(applicationId);
		ApplicationConfiguration newConfiguration = ApplicationConfiguration.from(configuration)
				.portRange(new PortRange(from, to)).build();
		repository.save(newConfiguration);

		// do we need to restart any containers?
		containers.restart(newConfiguration);

	}

	@Override
	public void clearPorts(String applicationId) throws ApplicationServiceException {
		ApplicationConfiguration configuration = repository.find(applicationId);
		ApplicationConfiguration newConfiguration = ApplicationConfiguration.from(configuration)
				.portRange(PortRange.NOT_RESTRICTED).build();
		repository.save(newConfiguration);
	}

}
