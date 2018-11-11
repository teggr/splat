package splat.core;

public interface ApplicationContainerService {

	ApplicationContainer get(String applicationId);

	ApplicationContainer start(ApplicationConfiguration configuration);

	ApplicationContainer stop(String applicationId);

	ApplicationContainer delete(String applicationId);

	ApplicationContainer restart(String applicationId);

}
