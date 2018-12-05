package splat.os.ports;

import java.util.Stack;

import lombok.extern.slf4j.Slf4j;
import splat.core.PortRange;

@Slf4j
public class Ports {

	private final Stack<Integer> portStack = new Stack<>();

	public Ports(PortRange portRange) {
		log.debug("Available ports {}", portRange);
		for (int i = portRange.getToInclusive(); i >= portRange.getFromInclusive(); i--) {
			portStack.push(i);
		}
	}

	public int allocate() throws NoMorePortsAvailableException {
		log.debug("Allocating port");
		if (portStack.isEmpty()) {
			throw new NoMorePortsAvailableException("There are no more ports available to allocate");
		}
		Integer port = portStack.pop();
		log.debug("Allocating new port {}", port);
		return port;
	}

	public void reserve(Integer port) throws PortNotAvailableException {
		log.debug("Reserving port {}", port);
		if (!portStack.remove(port)) {
			throw new PortNotAvailableException("The port " + port + " cannot be reserved. It is alread allocated.");
		}
	}

	public void deallocate(Integer port) {
		log.debug("Deallocating port {}", port);
		portStack.push(port);
	}

	public int allocateWithinRange(PortRange portRange)
			throws NoMorePortsAvailableException, PortNotAvailableException {
		log.debug("Allocating ports in {}", portRange);
		if (portRange.isNotRestricted()) {
			return allocate();
		}
		Stack<Integer> availablePorts = new Stack<>();
		availablePorts.addAll(portStack);
		availablePorts.removeIf(portRange::doesNotInclude);
		if (availablePorts.isEmpty()) {
			throw new NoMorePortsAvailableException("There are no more ports available in the range " + portRange);
		}
		Integer port = availablePorts.pop();
		reserve(port);
		log.debug("Allocating new port {}", port);
		return port;
	}

}
