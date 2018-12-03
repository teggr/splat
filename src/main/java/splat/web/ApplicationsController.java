package splat.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationService;
import splat.core.ApplicationServiceException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/applications")
public class ApplicationsController {

	private final ApplicationService applicationService;

	@GetMapping("/{id}")
	public String get(@PathVariable("id") String applicationId, Model model) throws ApplicationServiceException {
		model.addAttribute("app", applicationService.getApplication(applicationId));
		return "application";
	}

}
