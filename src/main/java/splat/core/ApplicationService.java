package splat.core;

import java.util.Set;

public interface ApplicationService {

	void create(ApplicationArtifact applicationJarResource) throws ApplicationServiceException;

	Set<Application> findAll();

}
