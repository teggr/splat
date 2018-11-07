package splat.web;

import java.io.IOException;
import java.io.InputStream;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationArtifact;

@RequiredArgsConstructor
public class UploadedFileArtifactAdapter implements ApplicationArtifact {

	private final TemporaryUploadedFile tmpFile;

	@Override
	public InputStream getInputStream() throws IOException {
		return tmpFile.getInputStream();
	}

	@Override
	public String getName() {
		return tmpFile.getName();
	}

}
