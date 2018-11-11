package splat.os.fs;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("splat.os.file-system")
class FileSystemProperties {

	private File homeDirectory;

}
