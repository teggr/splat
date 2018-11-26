package splat.os.fs;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import splat.core.ApplicationConfiguration;

@XmlRootElement(name = "applicationConfiguration")
@Getter
public class XmlApplicationConfiguration {

	private String applicationId;
	private String name;
	private String artifactName;

	public XmlApplicationConfiguration(ApplicationConfiguration configuration) {
		applicationId = configuration.getApplicationId();
		name = configuration.getName();
		artifactName = configuration.getArtifact().getName();
	}

	public XmlApplicationConfiguration() {
		// for deserialisation
	}

}
