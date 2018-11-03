package splat.core;

public interface ApplicationContainerCallback {

	void started(ApplicationContainer container);
	
	void failed(ApplicationContainer container);
	
}
