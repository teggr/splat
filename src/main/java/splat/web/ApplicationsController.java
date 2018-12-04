package splat.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	@PostMapping(path = "/{id}", params = "delete")
	public String delete(@PathVariable("id") String applicationId, RedirectAttributes redirectAttributes)
	        throws ApplicationServiceException {
		applicationService.delete(applicationId);
		redirectAttributes.addFlashAttribute("message", "You deleted the application " + applicationId + "!");
		return "redirect:/applications/" + applicationId;
	}

	@PostMapping(path = "/{id}", params = "restart")
	public String restart(@PathVariable("id") String applicationId, RedirectAttributes redirectAttributes)
	        throws ApplicationServiceException {
		applicationService.restart(applicationId);
		redirectAttributes.addFlashAttribute("message", "You restarted the application " + applicationId + "!");
		return "redirect:/applications/" + applicationId;
	}

	@PostMapping(path = "/{id}", params = "stop")
	public String stop(@PathVariable("id") String applicationId, RedirectAttributes redirectAttributes)
	        throws ApplicationServiceException {
		applicationService.stop(applicationId);
		redirectAttributes.addFlashAttribute("message", "You stopped the application " + applicationId + "!");
		return "redirect:/applications/" + applicationId;
	}

	@ExceptionHandler(Exception.class)
	public String handleException(@PathVariable("id") String applicationId, Exception e,
	        RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("message", e.getMessage());
		return "redirect:/applications/" + applicationId;
	}

}
