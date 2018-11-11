package splat.web;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.ApplicationArtifact;
import splat.core.Platform;
import splat.core.PlatformException;
import splat.core.TemporaryResource;
import splat.core.TemporaryUploadStore;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/application-upload")
public class ApplicationArtifactUploadController {

	private final Platform platform;

	private final TemporaryUploadStore uploadStore;

	@GetMapping
	public String get(Model model) throws IOException {
		return "application-upload";
	}

	@PostMapping
	public String post(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
			throws PlatformException, IOException {

		log.info("Uploaded a file {}", file.getOriginalFilename());

		TemporaryResource temporaryResource = uploadStore.save(file);

		log.info("Temporary resource created for upload {}", temporaryResource.getName());

		ApplicationArtifact applicationArtifact = new TemporaryResourceApplicationArtifactAdapter(temporaryResource);

		platform.createNew(applicationArtifact);

		redirectAttributes.addFlashAttribute("refresh", 5);
		redirectAttributes.addFlashAttribute("message",
				"You successfully created application " + applicationArtifact.getName() + "!");

		log.info("Redirecting back to home");

		return "redirect:/";

	}
	
	@ExceptionHandler(Exception.class)
	public String handleException(Exception e, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("message", e.getMessage());
		return "redirect:/application-upload";
	}

}