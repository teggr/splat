package splat.web;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
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
import splat.core.ApplicationJarResource;
import splat.core.ApplicationJarStorageService;
import splat.core.StorageFileNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/application-upload")
public class ApplicationUploadController {

	private final ApplicationJarStorageService storageService;

	@GetMapping
	public String get(Model model) throws IOException {

		log.info("Get all added files");

		model.addAttribute("files",
				storageService.loadAll().map(path -> path.getFileName().toString()).collect(Collectors.toList()));

		return "application-upload";

	}

	@PostMapping
	public String post(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
			throws IOException {

		log.info("Uploaded a file");

		ApplicationJarResource applicationJarResource = new MultipartFileApplicationJarResourceAdapter(file);

		storageService.store(applicationJarResource);

		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/application-upload";

	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}