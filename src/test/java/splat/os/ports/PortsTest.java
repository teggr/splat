package splat.os.ports;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import splat.core.PortRange;

public class PortsTest {

	@Test
	public void shouldAllocatePortsFromStartOfRange() throws NoMorePortsAvailableException {

		PortRange portRange = new PortRange(0, 10);

		Ports ports = new Ports(portRange);

		assertThat(ports.allocate(), is(0));

	}

	@Test
	public void shouldAllocatePortsInOrderDefinedByRange() throws NoMorePortsAvailableException {

		PortRange portRange = new PortRange(0, 2);

		Ports ports = new Ports(portRange);

		assertThat(ports.allocate(), is(0));
		assertThat(ports.allocate(), is(1));
		assertThat(ports.allocate(), is(2));

	}

	@Test(expected = NoMorePortsAvailableException.class)
	public void shouldNotAllocateAnyPortsWhenAllHaveBeenAllocated() throws NoMorePortsAvailableException {

		PortRange portRange = new PortRange(0, 1);

		Ports ports = new Ports(portRange);

		assertThat(ports.allocate(), is(0));
		assertThat(ports.allocate(), is(1));
		assertThat(ports.allocate(), is(2));

	}

	@Test
	public void shouldNotAllocateReservedPorts() throws PortNotAvailableException, NoMorePortsAvailableException {

		PortRange portRange = new PortRange(0, 2);

		Ports ports = new Ports(portRange);

		ports.reserve(1);

		assertThat(ports.allocate(), is(0));
		assertThat(ports.allocate(), is(2));

	}

	@Test(expected = PortNotAvailableException.class)
	public void shouldNotBeAbleToReservedPortsAlreadyReserved()
			throws PortNotAvailableException, NoMorePortsAvailableException {

		PortRange portRange = new PortRange(0, 2);

		Ports ports = new Ports(portRange);

		ports.reserve(1);

		ports.reserve(1);

	}

	@Test
	public void shouldReallocateAnyPortsDeallocated() throws PortNotAvailableException, NoMorePortsAvailableException {

		PortRange portRange = new PortRange(0, 0);

		Ports ports = new Ports(portRange);

		ports.reserve(0);
		ports.deallocate(0);

		assertThat(ports.allocate(), is(0));

	}

	@Test
	public void shouldAllocateAnyPortsIfRangeIsNotRestricted()
			throws PortNotAvailableException, NoMorePortsAvailableException {

		PortRange portRange = new PortRange(0, 4);

		Ports ports = new Ports(portRange);

		PortRange innerRange = PortRange.NOT_RESTRICTED;

		assertThat(ports.allocateWithinRange(innerRange), is(0));
		assertThat(ports.allocateWithinRange(innerRange), is(1));

	}

	@Test
	public void shouldAllocatePortsWithinRange() throws PortNotAvailableException, NoMorePortsAvailableException {

		PortRange portRange = new PortRange(0, 4);

		Ports ports = new Ports(portRange);

		PortRange innerRange = new PortRange(1, 2);

		assertThat(ports.allocateWithinRange(innerRange), is(1));
		assertThat(ports.allocateWithinRange(innerRange), is(2));

	}

}
