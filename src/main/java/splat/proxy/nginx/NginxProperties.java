package splat.proxy.nginx;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("splat.proxy.nginx")
public class NginxProperties {

	private File home;

}
