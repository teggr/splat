package splat.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.Application;
import splat.core.ApplicationArtifact;
import splat.core.ApplicationService;
import splat.core.TemporaryArtifactStore;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/applications")
public class ApplicationsController {

	private final ApplicationService applications;

	private final TemporaryArtifactStore uploadStore;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApplicationUploadCreatedResource postMultipartFile(@RequestParam("file") MultipartFile file)
			throws Exception {

		log.info("Uploaded a file via XHR {}", file.getOriginalFilename());

		ApplicationArtifact applicationArtifact = uploadStore.save(file);

		log.info("Created Temporary {} artifact {}", applicationArtifact.getType(), applicationArtifact.getName());

		Application application = applications.createFromArtifact(applicationArtifact);

		return new ApplicationUploadCreatedResource(
				"You successfully created application " + application.getId() + "!");

	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus
	@ResponseBody
	public ApplicationUploadCreatedResource handleException(Exception e) {
		return new ApplicationUploadCreatedResource("You failed to create an application " + e.getMessage() + "!");
	}

	@Data
	public static class ApplicationUploadCreatedResource {

		private final String message;

	}

}