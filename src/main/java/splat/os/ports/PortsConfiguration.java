package splat.os.ports;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortsConfiguration {

	@Bean
	public Ports ports(PortsProperties properties) {
		return new Ports(properties.getPortRange());
	}

}
