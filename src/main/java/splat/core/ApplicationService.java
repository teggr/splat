package splat.core;

import java.util.Set;

/**
 * {@link ApplicationService} consists of many {@link Application}s that may be
 * up and running. New {@link Application}s can be created from various types of
 * resource
 */
public interface ApplicationService {

	Application createFromArtifact(ApplicationArtifact applicationArtifact) throws ApplicationServiceException;

	Set<Application> getAllApplications() throws ApplicationServiceException;

	void delete(String applicationId) throws ApplicationServiceException;

	Application restart(String applicationId) throws ApplicationServiceException;

	Application stop(String applicationId) throws ApplicationServiceException;

	Application getApplication(String applicationId) throws ApplicationServiceException;

	void fixPorts(String applicationId, int from, int to) throws ApplicationServiceException;

	void clearPorts(String applicationId) throws ApplicationServiceException;

}
