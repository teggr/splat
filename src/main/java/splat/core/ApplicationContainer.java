package splat.core;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApplicationContainer {

	private final String name;
	private final String status;
	
}
