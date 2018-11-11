package splat.os;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import splat.core.TemporaryUploadStore;

@Slf4j
@Component
public class FileSystemTemporaryDirectory implements TemporaryUploadStore {

	// TODO: run clean up job
	@Override
	public FileSystemTemporaryResource save(MultipartFile file) throws IOException {
		File temporaryFile = File.createTempFile("spl", "up");
		FileUtils.copyInputStreamToFile(file.getInputStream(), temporaryFile);
		log.info("For upload {} a temporary file has been created {}", file.getOriginalFilename(),
				temporaryFile.getAbsolutePath());
		return new FileSystemTemporaryResource(file.getOriginalFilename(), temporaryFile);
	}

}
