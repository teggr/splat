package splat.core;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface TemporaryArtifactStore {

	ApplicationArtifact save(MultipartFile file) throws IOException;

}
