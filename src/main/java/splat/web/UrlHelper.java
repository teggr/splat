package splat.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationContainer;

@RequiredArgsConstructor
public class UrlHelper {

	private static final String X_FORWARDED_FOR_HTTP_HEADER_NAME = "X-Forwarded-For";

	private final HttpServletRequest request;

	public String resolve(ApplicationContainer applicationContainer) {

		// need to determine if we are a forwarded call or not
		UriComponentsBuilder builder = ServletUriComponentsBuilder.fromContextPath(request)
				.replacePath(applicationContainer.getContextPath());
		if (requestFromProxy()) {
			return builder.build().toUriString();
		} else {
			return builder.port(applicationContainer.getServerPort()).build().toUriString();
		}

	}

	private boolean requestFromProxy() {
		return request.getHeader(X_FORWARDED_FOR_HTTP_HEADER_NAME) != null;
	}

}
