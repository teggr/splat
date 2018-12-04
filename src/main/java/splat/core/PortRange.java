package splat.core;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Value;

@Value
public class PortRange {

	public static final PortRange NOT_RESTRICTED = new PortRange(-1, -1);

	private final int fromInclusive;
	private final int toInclusive;

	public boolean isNotRestricted() {
		return fromInclusive == -1 && toInclusive == -1;
	}

	public Collection<Integer> asCollection() {
		Collection<Integer> list = new ArrayList<>();
		for (int i = fromInclusive; i <= toInclusive; i++) {
			list.add(i);
		}
		return list;
	}

}
