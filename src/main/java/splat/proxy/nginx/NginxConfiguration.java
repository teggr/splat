package splat.proxy.nginx;

import java.nio.charset.StandardCharsets;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class NginxConfiguration {

	@Bean
	@Order
	public SpringResourceTemplateResolver nginxTemplateResolver(ApplicationContext applicationContext) {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("classpath:/nginx/");
		resolver.setSuffix(".conf");
		resolver.setTemplateMode(TemplateMode.TEXT);
		resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		resolver.setCacheable(true);
		resolver.setCheckExistence(true);
		resolver.setOrder(2);
		return resolver;
	}

}
