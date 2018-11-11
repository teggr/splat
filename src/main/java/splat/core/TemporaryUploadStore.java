package splat.core;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface TemporaryUploadStore {

	TemporaryResource save(MultipartFile file) throws IOException;

}
