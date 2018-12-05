package splat.core;

import lombok.Value;

@Value
public class PortRange {

	public static final PortRange NOT_RESTRICTED = new PortRange(-1, -1);

	private final int fromInclusive;
	private final int toInclusive;

	public boolean isNotRestricted() {
		return fromInclusive == -1 && toInclusive == -1;
	}

	@Override
	public String toString() {
		if (isNotRestricted()) {
			return "Any";
		} else {
			return fromInclusive + ":" + toInclusive;
		}
	}

	public boolean includes(int port) {
		return port >= fromInclusive && port <= toInclusive;
	}

	public boolean doesNotInclude(int port) {
		return !includes(port);
	}

}
