package splat.os.ports;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import splat.core.PortRange;

@Data
@Component
@ConfigurationProperties("splat.os.ports")
public class PortsProperties {

	private int fromInclusive = 9091;
	private int toInclusive = 10091;

	public PortRange getPortRange() {
		return new PortRange(fromInclusive, toInclusive);
	}

}
