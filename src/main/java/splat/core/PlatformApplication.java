package splat.core;

import lombok.Builder;

/**
 * {@link PlatformApplication} is a holistic view over an {@link Application}
 * and any associated {@link ApplicationContainer}
 */
@Builder
public class PlatformApplication implements Comparable<PlatformApplication> {

	private final Application application;
	private final ApplicationContainer container;

	@Override
	public int compareTo(PlatformApplication o) {
		return o.application.compareTo(this.application);
	}

	public String getName() {
		return application.getName();
	}

	public String getStatus() {
		return container.getStatus();
	}
	
	public boolean isAlive() {
		return container.isAlive();
	}
	
	public boolean isRestartable() {
		return container.isRestartable();
	}
	
	public boolean isDeletable() {
		return container.isDeletable();
	}
	
	public boolean isStoppable() {
		return container.isStoppable();
	}

}
