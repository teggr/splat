package splat.core;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProcessRuntimeScheduler implements RuntimeScheduler {

	@Override
	public void scheduleApplication(Application application) {

		log.info("Scheduling {}" + application.getName());

	}

}
