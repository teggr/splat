package splat.core;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("!proxy")
public class NoProxyService implements ProxyService {

	@Override
	public void start(int port, String contextPath) {
		log.info("start Proxy - no proxy configured");
	}

	@Override
	public void remove(String contextPath) {
		log.info("start Proxy - no proxy configured");
	}

}
