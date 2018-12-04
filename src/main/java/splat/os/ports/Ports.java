package splat.os.ports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import lombok.extern.slf4j.Slf4j;
import splat.core.PortRange;

@Slf4j
public class Ports {

	private final Stack<Integer> portStack = new Stack<>();

	public Ports(int fromInclusive, int toInclusive) {
		log.info("Allocating ports from {} to {}", fromInclusive, toInclusive);
		for (int i = toInclusive; i >= fromInclusive; i--) {
			portStack.add(i);
		}
	}

	public int allocate() {
		Integer port = portStack.pop();
		log.info("Allocating new port {}", port);
		return port;
	}

	public void allocate(Integer port) {
		log.info("Allocating existing port {}", port);
		portStack.remove(port);
	}

	public void deallocate(Integer port) {
		log.info("Deallocating port {}", port);
		portStack.push(port);
	}

	public int allocateFrom(PortRange portRange) {
		if(portRange.isNotRestricted()) {
			return allocate();
		}
		ArrayList<Integer> availablePorts = new ArrayList<>(portStack);
		Collection<Integer> ports = portRange.asCollection();
		ports.removeAll(availablePorts);
		java.lang.Integer thePort = ports.stream().findFirst().get();
		allocate(thePort);
		return thePort;
	}

}
