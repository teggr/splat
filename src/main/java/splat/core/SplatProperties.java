package splat.core;

import java.net.URL;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("splat")
public class SplatProperties {

	private URL location;

}
