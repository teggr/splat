package splat.core;

import java.util.stream.Stream;

public interface ApplicationConfigurationRepository {

	Stream<ApplicationConfiguration> findAll();

	ApplicationConfiguration find(String id);

	ApplicationConfiguration save(ApplicationConfiguration id);	

	void remove(String id);

	boolean exists(String id);

}
