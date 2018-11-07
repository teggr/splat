package splat.core;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProcessRuntimeScheduler implements RuntimeScheduler {

	@NonNull
	private final TaskExecutor executor;
	
	@Override
	public void scheduleTask(ProcessTask task) {
		log.info("Scheduling task {}", task.getName());
		executor.execute(task);
	}
	
}
