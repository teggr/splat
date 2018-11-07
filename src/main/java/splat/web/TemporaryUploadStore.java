package splat.web;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface TemporaryUploadStore {

	TemporaryUploadedFile save(MultipartFile file) throws IOException;

}
