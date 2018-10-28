package splat.core;

import java.util.List;
import java.util.Set;

/**
 * {@link Platform}							
 */
public interface Platform {

	void init() throws PlatformException;

	void createNew(ApplicationArtifact applicationArtifact) throws PlatformException;

	Set<Application> findAllApplications();
	
}
