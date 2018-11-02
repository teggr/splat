package splat.core;

public interface RuntimeService {

	ApplicationContainer getContainer(String name);

	void deploy(Application application);

}
