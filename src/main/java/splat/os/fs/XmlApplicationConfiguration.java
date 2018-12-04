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
	private int portRangeFromInclusive = -1;
	private int portRangeToInclusive = -1;

	public XmlApplicationConfiguration(ApplicationConfiguration configuration) {
		applicationId = configuration.getApplicationId();
		name = configuration.getName();
		artifactName = configuration.getArtifact().getName();
		portRangeFromInclusive = configuration.getPortRange().getFromInclusive();
		portRangeToInclusive = configuration.getPortRange().getToInclusive();
	}

	public XmlApplicationConfiguration() {
		// for deserialisation
	}

}
