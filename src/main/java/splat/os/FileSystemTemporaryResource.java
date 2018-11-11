package splat.os;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.RequiredArgsConstructor;
import splat.core.TemporaryResource;

@RequiredArgsConstructor
public class FileSystemTemporaryResource implements TemporaryResource {
	
	private final String name;
	private final File file;

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	public String getName() {
		return name;
	}

}
