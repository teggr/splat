package splat.core;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import splat.web.TemporaryUploadStore;
import splat.web.TemporaryUploadedFile;

@Slf4j
@Component
public class TemporaryDirectoryUploadStore implements TemporaryUploadStore {

	// TODO: run clean up job
	@Override
	public TemporaryUploadedFile save(MultipartFile file) throws IOException {
		File temporaryFile = File.createTempFile("spl", "up");
		FileUtils.copyInputStreamToFile(file.getInputStream(), temporaryFile);
		log.info("For upload {} a temporary file has been created {}", file.getOriginalFilename(),
				temporaryFile.getAbsolutePath());
		return new TemporaryUploadedFile(file.getOriginalFilename(), temporaryFile);
	}

}
