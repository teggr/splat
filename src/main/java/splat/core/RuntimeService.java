package splat.core;

import java.io.IOException;

public interface RuntimeService {

	ApplicationContainer getContainer(String name);

	void deploy(ApplicationConfiguration application);

	void init() throws IOException;

	void delete(String appName);

	void restart(ApplicationConfiguration application);

	void stop(ApplicationConfiguration find);

}
