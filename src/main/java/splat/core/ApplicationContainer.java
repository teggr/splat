package splat.core;

import lombok.Builder;
import lombok.Getter;

import org.zeroturnaround.process.SystemProcess;

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
	private final SystemProcess process;
	
	public String getStatus() {
		return status.toString();
	}
	
	public boolean isAlive() {
		try {
			return process != null && process.isAlive();			
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isRestartable() {
		return true;
	}

	public boolean isDeletable() {
		return !isAlive();
	}

	public boolean isStoppable() {
		return isAlive();
	}
	
}
