package splat.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileArtifactAdapter implements ApplicationArtifact {

	private final File file;

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public String getName() {
		return file.getName();
	}

}
