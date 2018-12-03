package splat.core;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationConfiguration implements Comparable<ApplicationConfiguration> {

	private final String name;
	private final String applicationId;
	@Builder.Default
	private PortRange portRange = PortRange.NOT_RESTRICTED;
	private final ApplicationArtifact artifact;

	@Override
	public int compareTo(ApplicationConfiguration o) {
		return this.applicationId.compareToIgnoreCase(o.applicationId);
	}

}
