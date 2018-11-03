package splat.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SplatApplicationService implements ApplicationService {

	@NonNull
	private final SplatEnvironment environment;
	private File applicationsDirectory;

	@Override
	public void init() throws IOException {
		applicationsDirectory = new File(environment.getHomeDirectory(), "applications");
		if (!applicationsDirectory.exists() && !applicationsDirectory.mkdirs()) {
			throw new IOException(
					"Could not create applications directory " + applicationsDirectory + " with parent directorys");
		}
	}

	@Override
	public Set<Application> findAll() {
		File[] listFiles = applicationsDirectory.listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
		return Stream.of(listFiles).map(SplatApplicationService::toApplication).collect(Collectors.toSet());
	}

	@Override
	public Application find(String appName) throws ApplicationServiceException {
		return findAll().stream().filter(f -> f.getName().equals(appName)).findFirst().get();
	}

	private static Application toApplication(File directory) {
		String baseName = FilenameUtils.getBaseName(directory.getName());
		return Application.builder().name(baseName)
				.artifact(new FileArtifactAdapter(new File(directory, baseName + ".jar"))).build();
	}

	@Override
	public Application create(ApplicationArtifact applicationArtifact) throws ApplicationServiceException {

		try {

			String name = applicationArtifact.getName();

			String baseName = FilenameUtils.getBaseName(name);

			// create a new application folder from artifact name
			File applicationFolder = new File(applicationsDirectory, baseName);
			if (!applicationFolder.mkdirs()) {
				throw new IOException("Could not create application folder " + applicationFolder);
			}

			File artifactFile = new File(applicationFolder, name);

			FileUtils.copyInputStreamToFile(applicationArtifact.getInputStream(), artifactFile);

			return Application.builder().name(baseName).artifact(new FileArtifactAdapter(artifactFile)).build();

		} catch (IOException e) {
			log.error("{}", e.getMessage(), e);
			throw new ApplicationServiceException(e);
		}

	}

	@Override
	public void delete(String appName) throws ApplicationServiceException {

		try {
			File applicationFolder = new File(applicationsDirectory, appName);
			FileUtils.deleteDirectory(applicationFolder);
		} catch (IOException e) {
			log.error("{}", e.getMessage(), e);
			throw new ApplicationServiceException(e);
		}

	}

}
