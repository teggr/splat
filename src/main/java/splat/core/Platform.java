package splat.core;

import java.util.Set;

/**
 * {@link Platform} consists of many {@link PlatformApplication}s that may be up
 * and running. New {@link PlatformApplication}s can be created from various
 * types of resource
 */
public interface Platform {

	void init() throws PlatformException;

	void createNew(ApplicationArtifact applicationArtifact) throws PlatformException;

	Set<PlatformApplication> getAllApplications();

}
