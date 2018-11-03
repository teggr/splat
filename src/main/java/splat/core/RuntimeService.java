package splat.core;

import java.io.IOException;

public interface RuntimeService {

	ApplicationContainer getContainer(String name);

	void deploy(Application application);

	void init() throws IOException;

	void delete(String appName);

}
