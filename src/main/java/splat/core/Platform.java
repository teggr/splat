package splat.core;

import java.util.Set;

/**
 * {@link Platform} consists of many {@link PlatformApplication}s that may be up
 * and running. New {@link PlatformApplication}s can be created from various
 * types of resource
 */
public interface Platform {

	void createNewApplicationFromArtifact(ApplicationArtifact applicationArtifact) throws PlatformException;

	Set<PlatformApplication> getAllApplications() throws PlatformException;

	void delete(String appName) throws PlatformException;

	void restart(String appName) throws PlatformException;

	void stop(String appName) throws PlatformException;

}
