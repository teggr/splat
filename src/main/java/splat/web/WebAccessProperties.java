package splat.web;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class WebAccessProperties {

	@NotNull
	private String username;
	@NotNull
	private String password;
	
}
