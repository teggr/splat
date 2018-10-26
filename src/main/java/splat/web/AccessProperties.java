package splat.web;

import java.nio.file.Path;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AccessProperties {

	@NotNull
	private String username;
	@NotNull
	private String password;
	
}
