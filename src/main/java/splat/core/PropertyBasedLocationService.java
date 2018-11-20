package splat.core;

import java.net.URL;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PropertyBasedLocationService implements LocationService {

	private final SplatProperties splatProperties;

	@Override
	public URL getLocation() {
		return splatProperties.getLocation();
	}

	@Override
	public URL getLocation(int port, String contextPath) {
		try {
			return ServletUriComponentsBuilder.fromUri(splatProperties.getLocation().toURI()).replacePath(contextPath)
					.build().toUri().toURL();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not get location for contextPath " + contextPath, e);
		}
	}

}
