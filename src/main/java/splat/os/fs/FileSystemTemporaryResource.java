package splat.os.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationArtifact;

@RequiredArgsConstructor
class FileSystemTemporaryResource implements ApplicationArtifact {
	
	private final String name;
	private final String type;
	private final File file;
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getType() {
		return type;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

}
