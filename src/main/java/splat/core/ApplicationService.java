package splat.core;

import java.util.Set;

public interface ApplicationService {

	Application create(ApplicationArtifact applicationJarResource) throws ApplicationServiceException;

	Set<Application> findAll();

}
