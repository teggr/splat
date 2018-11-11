package splat.os.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationArtifact;

@RequiredArgsConstructor
class FileArtifactAdapter implements ApplicationArtifact {

	private final File file;

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public String getName() {
		return FilenameUtils.getBaseName(file.getName());
	}

	@Override
	public String getType() {
		return FilenameUtils.getExtension(file.getName());
	}

}
