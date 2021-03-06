package splat.core;

import java.util.Collection;

public interface ApplicationContainerService {

	ApplicationContainer get(String applicationId);

	ApplicationContainer start(ApplicationConfiguration configuration);

	ApplicationContainer stop(String applicationId);

	ApplicationContainer delete(String applicationId);

	ApplicationContainer restart(String applicationId);

	Collection<ApplicationContainer> getAll();

}
