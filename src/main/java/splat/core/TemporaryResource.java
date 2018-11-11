package splat.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author robin 
 */
public interface TemporaryResource {

	InputStream getInputStream() throws IOException;

	String getName();

}
