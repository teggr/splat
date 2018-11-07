package splat.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TemporaryUploadedFile {
	
	private final String name;
	private final File file;

	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	public String getName() {
		return name;
	}

}
