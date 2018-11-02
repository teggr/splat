package splat.core;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SplatPlatform implements Platform {

	private final SplatProperties properties;

	private File homeDirectory;

	private ApplicationService applicationService;
	private RuntimeService runtimeService;

	public SplatPlatform(SplatProperties properties) {
		this.properties = properties;
		Objects.requireNonNull(properties, "Splat platform requires properties for configuration");
	}

	@Override
	public void init() throws PlatformException {

		try {

			homeDirectory = properties.getHomeDirectory();
			if (homeDirectory == null) {
				homeDirectory = new File(System.getProperty("user.home"), "/.splat");
			}

			log.info("Using home directory {}", homeDirectory);

			if (!homeDirectory.exists() && !homeDirectory.mkdirs()) {
				throw new IOException("Could not create home directory " + homeDirectory + " with parent directorys");
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

		try {

			File runtimeDirectory = new File(homeDirectory, "runtime");
			if (!runtimeDirectory.exists() && !runtimeDirectory.mkdirs()) {
				throw new IOException(
						"Could not create runtime directory " + runtimeDirectory + " with parent directorys");
			}
			runtimeService = new SplatRuntimeService(runtimeDirectory);

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
	public Set<PlatformApplication> getAllApplications() {

		return applicationService.findAll().stream().map(this::toPlatformApplication).collect(Collectors.toSet());

	}

	private PlatformApplication toPlatformApplication(Application app) {
		return PlatformApplication.builder().application(app).container(runtimeService.getContainer(app.getName()))
				.build();
	}

}
