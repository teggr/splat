package splat.core;

import java.util.Set;

public interface ApplicationService {

	void create(ApplicationJarResource applicationJarResource) throws ApplicationServiceException;

	Set<Application> findAll();

}
