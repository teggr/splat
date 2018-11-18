package splat.proxy.nginx;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.ProxyService;
import splat.os.fs.FileSystemEnvironment;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty("splat.proxy.nginx.home")
public class NginxProxyService implements ProxyService, InitializingBean {

	private final TemplateEngine templateEngine;
	private final NginxProperties properties;
	private final FileSystemEnvironment environment;

	private File proxyLocation;

	@Override
	public void afterPropertiesSet() throws Exception {

		log.info("Initialising Application Container directory");

		proxyLocation = new File(environment.getHomeDirectory(), "proxy");
		if (!proxyLocation.exists() && !proxyLocation.mkdirs()) {
			throw new IOException(
					"Could not create nginx proxy directory " + proxyLocation + " with parent directorys");
		}

	}

	@Override
	public void start(int port, String contextPath) {

		String locationMatch = contextPath;
		String uri = "http://localhost:" + port;
		LocationBlock location = new LocationBlock(locationMatch, uri, uri);
		Context context = new Context();
		context.setVariable("location", location);
		String conf = templateEngine.process("location-template", context);

		File contextProxy = new File(proxyLocation, contextPath + ".conf");
		try {
			FileUtils.writeStringToFile(contextProxy, conf, Charset.defaultCharset());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		// run an nginz proces reload
		File home = properties.getHome();

		ProcessBuilder processBuilder = new NginxExecutable(home.getAbsolutePath()).processBuilder("-s", "reload")
				.directory(home);
		try {
			processBuilder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void remove(String contextPath) {

		File contextProxy = new File(proxyLocation, contextPath + ".conf");

		contextProxy.delete();

		// run an nginz proces reload
		File home = properties.getHome();

		ProcessBuilder processBuilder = new NginxExecutable(home.getAbsolutePath()).processBuilder("-s", "reload")
				.directory(home);
		try {
			processBuilder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
