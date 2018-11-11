package splat.web;

import java.io.IOException;
import java.io.InputStream;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationArtifact;
import splat.core.TemporaryResource;

@RequiredArgsConstructor
public class TemporaryResourceApplicationArtifactAdapter implements ApplicationArtifact {

	private final TemporaryResource temporaryResource;

	@Override
	public InputStream getInputStream() throws IOException {
		return temporaryResource.getInputStream();
	}

	@Override
	public String getName() {
		return temporaryResource.getName();
	}

}
