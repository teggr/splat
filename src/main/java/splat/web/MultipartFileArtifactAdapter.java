package splat.web;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationArtifact;

@RequiredArgsConstructor
public class MultipartFileArtifactAdapter implements ApplicationArtifact {

	private final MultipartFile file;

	@Override
	public InputStream getInputStream() throws IOException {
		return file.getInputStream();
	}

	@Override
	public String getName() {
		return file.getOriginalFilename();
	}

}
