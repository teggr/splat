package splat.core;

import java.io.IOException;
import java.util.Set;

public interface ApplicationService {

	Application create(ApplicationArtifact applicationJarResource) throws ApplicationServiceException;

	Set<Application> findAll();

	void init() throws IOException;

	void delete(String appName) throws ApplicationServiceException;

	Application find(String appName) throws ApplicationServiceException;

}
