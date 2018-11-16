package splat.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link Application} is a holistic view over an
 * {@link ApplicationConfiguration} and any associated
 * {@link ApplicationContainer}s that contain running instances of an
 * {@link ApplicationConfiguration}
 */
@RequiredArgsConstructor
@Getter
public class Application implements Comparable<Application> {

	@NonNull
	private final ApplicationConfiguration configuration;
	@NonNull
	private final ApplicationContainer container;

	@Override
	public int compareTo(Application o) {
		return o.getId().compareTo(this.getId());
	}

	public String getId() {
		return configuration.getApplicationId();
	}

}
