package splat.core;

import java.util.Properties;

import org.zeroturnaround.process.SystemProcess;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApplicationContainer {

	public enum ContainerState {
		UNKNOWN, DEPLOY_FAILED, RUN_FAILED, RUNNING, STOPPED;
	}

	private final String name;
	@Getter(AccessLevel.NONE)
	private final ContainerState status;
	private final SystemProcess process;
	private final Properties properties;

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

	public static ApplicationContainer empty() {
		return ApplicationContainer.builder().status(ContainerState.UNKNOWN).build();
	}

}
