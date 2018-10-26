package splat.core;

import java.nio.file.Path;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class StorageProperties {

	@NotNull
	private Path path;
	
}
