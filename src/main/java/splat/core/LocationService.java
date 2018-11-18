package splat.core;

import java.net.URL;

public interface LocationService {

	URL getLocation();

	URL getLocation(int port, String contextPath);

}
