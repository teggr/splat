package splat.core;

import java.util.stream.Stream;

public interface ApplicationRepository {

	Stream<Application> findAll();

	Application findByName(String name);

	Application save(Application application);

	void removeByName(String name);

}
