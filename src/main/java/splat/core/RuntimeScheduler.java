package splat.core;

import java.io.File;

public interface RuntimeScheduler {

	public void scheduleApplication(Application application, File runtimeDirectory,
			ApplicationContainerCallback applicationContainerCallback);

	public void restartApplication(Application application, File runtimeDirectory,
			ApplicationContainerCallback applicationContainerCallback);

}
