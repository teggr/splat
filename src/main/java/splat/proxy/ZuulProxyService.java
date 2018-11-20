package splat.proxy;

import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.ApplicationContainer;
import splat.core.ApplicationContainerProcessor;
import splat.core.ProxyService;

@Slf4j
@RequiredArgsConstructor
@Component
public class ZuulProxyService implements ProxyService, ApplicationEventPublisherAware, ApplicationContainerProcessor {

	private final DiscoveryClientRouteLocator locator;
	private final ZuulProperties zuulProperties;
	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void process(ApplicationContainer container) {

		log.info("processing container {}", container);
		start(container.getServerPort(), container.getContextPath());

	}

	@Override
	public void start(int port, String contextPath) {

		log.info("add proxy route {}", contextPath);
		zuulProperties.getRoutes().put(contextPath,
				new ZuulRoute(contextPath + "/**", "http://localhost:" + port + "/"));
		applicationEventPublisher.publishEvent(new RoutesRefreshedEvent(locator));

	}

	@Override
	public void remove(String contextPath) {

		log.info("remove proxy root {}", contextPath);
		zuulProperties.getRoutes().remove(contextPath);
		applicationEventPublisher.publishEvent(new RoutesRefreshedEvent(locator));

	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

}
