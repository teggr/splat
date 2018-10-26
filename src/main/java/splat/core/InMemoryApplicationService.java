package splat.core;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class InMemoryApplicationService implements ApplicationService {

	private final ApplicationJarStorageService storageService;

	private Set<Application> applications = new TreeSet<>();

	@Override
	public Set<Application> findAll() {
		return Collections.unmodifiableSet(applications);
	}

	@Override
	public void create(ApplicationJarResource applicationJarResource) throws ApplicationServiceException {

		try {
			storageService.store(applicationJarResource);
		} catch (IOException e) {
			log.error("{}", e.getMessage(), e);
			throw new ApplicationServiceException(e);
		}

		// after this an application should have been created or updated
		Application application = Application.builder().name(applicationJarResource.getApplicationName())
				.status("Uploaded").build();

		applications.add(application);

	}

}
