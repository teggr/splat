package splat.core;

public interface RuntimeScheduler {

	void scheduleTask(ProcessTask task);

	public static interface ProcessTask extends Runnable {
		String getName();
	}

}
