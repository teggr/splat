package splat.core;

import java.io.IOException;
import java.io.InputStream;

public interface ApplicationJarResource {

	InputStream getInputStream() throws IOException;

	String getApplicationName();

}
