package splat.core;

import java.net.URL;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PropertyBasedLocationService implements LocationService {

	private final SplatProperties splatProperties;
	private final ServerProperties serverProperties;

	@Override
	public URL getLocation() {
		return splatProperties.getLocation();
	}

	@Override
	public URL getLocation(int port, String contextPath) {
		try {
			return ServletUriComponentsBuilder.fromUri(splatProperties.getLocation().toURI()).port(overridePort(port))
					.replacePath(contextPath).build().toUri().toURL();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not get location for contextPath " + contextPath, e);
		}
	}

	private int overridePort(int port) {
		int locationPort = splatProperties.getLocation().getPort();
		if (locationPort == -1) {
			return -1;
		}
		return port;
	}

}
