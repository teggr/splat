package splat.os.fs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import splat.core.ApplicationArtifact;
import splat.core.TemporaryArtifactStore;

@Slf4j
@Component
class FileSystemTemporaryDirectory implements TemporaryArtifactStore {

	// TODO: run clean up job

	@Override
	public ApplicationArtifact save(MultipartFile file) throws IOException {
		File temporaryFile = File.createTempFile("spl", "up");
		file.transferTo(temporaryFile);
		log.info("For upload {} a temporary file of size {} has been created {}", file.getOriginalFilename(), temporaryFile.length(),
				temporaryFile.getAbsolutePath());
		return new FileSystemTemporaryResource(FilenameUtils.getBaseName(file.getOriginalFilename()),
				FilenameUtils.getExtension(file.getOriginalFilename()), temporaryFile);
	}

}
