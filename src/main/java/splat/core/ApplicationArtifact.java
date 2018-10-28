package splat.core;

import java.io.IOException;
import java.io.InputStream;

public interface ApplicationArtifact {

	InputStream getInputStream() throws IOException;

	String getName();

}
