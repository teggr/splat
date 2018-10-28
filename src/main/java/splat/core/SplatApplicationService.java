package splat.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SplatApplicationService implements ApplicationService {

	private final File applicationsDirectory;

	public SplatApplicationService(File applicationsDirectory) {
		Objects.requireNonNull(applicationsDirectory);
		this.applicationsDirectory = applicationsDirectory;
	}

	@Override
	public Set<Application> findAll() {
		File[] listFiles = applicationsDirectory.listFiles((FileFilter)FileFilterUtils.directoryFileFilter());
		return Stream.of(listFiles).map(SplatApplicationService::toApplication).collect(Collectors.toSet());
	}

	private static Application toApplication(File f) {
		String baseName = FilenameUtils.getBaseName(f.getName());
		return Application.builder().name(baseName).status("Loaded").build();
	}

	@Override
	public void create(ApplicationArtifact applicationArtifact) throws ApplicationServiceException {

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

		} catch (IOException e) {
			log.error("{}", e.getMessage(), e);
			throw new ApplicationServiceException(e);
		}

	}

}
