package splat.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SplatPlatform implements Platform {

	private final SplatProperties properties;

	private File homeDirectory;

	private ApplicationService applicationService;

	public SplatPlatform(SplatProperties properties) {
		this.properties = properties;
		Objects.requireNonNull(properties, "Splat platform requires properties for configuration");
	}

	@Override
	public void init() throws PlatformException {

		try {

			homeDirectory = properties.getHomeDirectory();

			if (properties.isCreateHomeDirectory()) {
				if (!homeDirectory.exists() && !homeDirectory.mkdirs()) {
					throw new IOException(
							"Could not create home directory " + homeDirectory + " with parent directorys");
				}
			}

			if (!homeDirectory.exists()) {
				throw new IOException("Home directory " + homeDirectory + " does not exist");
			}

		} catch (IOException e) {
			throw new PlatformException("Could not create Splat home directory", e);
		}

		try {

			File applicationsDirectory = new File(homeDirectory, "applications");
			if (!applicationsDirectory.exists() && !applicationsDirectory.mkdirs()) {
				throw new IOException(
						"Could not create applications directory " + applicationsDirectory + " with parent directorys");
			}
			applicationService = new SplatApplicationService(applicationsDirectory);

		} catch (IOException e) {
			throw new PlatformException("Could not create Splat applications directory", e);
		}

	}

	@Override
	public void createNew(ApplicationArtifact applicationArtifact) throws PlatformException {
		try {
			applicationService.create(applicationArtifact);
		} catch (ApplicationServiceException e) {
			throw new PlatformException(e);
		}
	}

	@Override
	public Set<Application> findAllApplications() {
		return applicationService.findAll();
	}

}
