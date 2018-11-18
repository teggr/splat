package splat.proxy.nginx;

import lombok.Data;

@Data
public class LocationBlock {

	private final String locationMatch;
	private final String proxyPass;
	private final String proxyRedirect;

}
