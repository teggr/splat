package splat.core;

import lombok.Builder;
import lombok.Getter;
import lombok.AccessLevel;

@Builder
@Getter
public class ApplicationContainer {
	
	enum ContainerState {
		UNKNOWN, DEPLOY_FAILED, RUN_FAILED, RUNNING, STOPPED;
	}

	private final String name;
	@Getter(AccessLevel.NONE)
	private final ContainerState status;
	private final Process process;
	
	public String getStatus() {
		return status.toString();
	}
	
	public boolean isAlive() {
		return process != null && process.isAlive();
	}

	public boolean isRestartable() {
		return true;
	}

	public boolean isDeletable() {
		return true;
	}

	public boolean isStoppable() {
		return isAlive();
	}
	
}
