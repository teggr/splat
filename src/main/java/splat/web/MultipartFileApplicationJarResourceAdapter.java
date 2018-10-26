package splat.web;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationJarResource;

@RequiredArgsConstructor
public class MultipartFileApplicationJarResourceAdapter implements ApplicationJarResource {

	private final MultipartFile file;

	@Override
	public InputStream getInputStream() throws IOException {
		return file.getInputStream();
	}

	@Override
	public String getApplicationName() {
		return file.getOriginalFilename();
	}

}
