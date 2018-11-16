package splat.core;

import java.util.Optional;
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
	private ContainerState status;
	private final SystemProcess process;
	private final Properties properties;
	private final Starter starter;

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
		return ApplicationContainer.builder().status(ContainerState.UNKNOWN).properties(new Properties())
				.starter(new Starter() {
					@Override
					public void start() throws Exception {

					}
				}).build();
	}

	public static ApplicationContainerBuilder from(ApplicationContainer container) {
		return builder().name(container.name).status(container.status).process(container.process)
				.properties(container.properties).starter(container.starter);
	}

	public void start() throws Exception {
		try {
			starter.start();
			status = ContainerState.RUNNING;
		} catch (Exception e) {
			status = ContainerState.RUN_FAILED;
			throw e;
		}
	}

	public Integer getServerPort() {
		return Integer.parseInt(Optional.ofNullable(properties)
				.map(p -> p.getProperty(SpringBootApplicationProperties.SERVER_PORT)).orElse("0"));
	}

	public String getContextPath() {
		return Optional.ofNullable(properties)
				.map(p -> p.getProperty(SpringBootApplicationProperties.SERVER_SERVLET_CONTEXT_PATH)).orElse("/");
	}

}
