package splat.web;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import splat.core.ApplicationJarResource;
import splat.core.ApplicationService;
import splat.core.ApplicationServiceException;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/application-upload")
public class ApplicationUploadController {

	private final ApplicationService applicationService;

	@GetMapping
	public String get(Model model) throws IOException {

		return "application-upload";

	}

	@PostMapping
	public String post(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
			throws ApplicationServiceException {

		log.info("Uploaded a file {}", file.getOriginalFilename());

		ApplicationJarResource applicationJarResource = new MultipartFileApplicationJarResourceAdapter(file);

		applicationService.create(applicationJarResource);

		redirectAttributes.addFlashAttribute("message",
				"You successfully created application " + applicationJarResource.getApplicationName() + "!");

		return "redirect:/";

	}

}