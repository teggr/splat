package splat.core;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Application implements Comparable<Application> {

	private final String name;
	private final String status;

	@Override
	public int compareTo(Application o) {
		return this.name.compareToIgnoreCase(o.name);
	}

}
