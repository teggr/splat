package splat.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationService;
import splat.core.ApplicationServiceException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {

	private final ApplicationService applications;

	@GetMapping
	public String get(HttpServletRequest request, Model model) {
		try {
			model.addAttribute("applications", applications.getAllApplications());
		} catch (ApplicationServiceException e) {
			model.addAttribute("message", e.getMessage());
		}
		return "index";
	}

	@PostMapping(params = "delete")
	public String delete(@RequestParam("applicationId") String applicationId, RedirectAttributes redirectAttributes)
			throws ApplicationServiceException {
		applications.delete(applicationId);
		redirectAttributes.addFlashAttribute("message", "You deleted the application " + applicationId + "!");
		return "redirect:/";
	}

	@PostMapping(params = "restart")
	public String restart(@RequestParam("applicationId") String applicationId, RedirectAttributes redirectAttributes)
			throws ApplicationServiceException {
		applications.restart(applicationId);
		redirectAttributes.addFlashAttribute("message", "You restarted the application " + applicationId + "!");
		return "redirect:/";
	}

	@PostMapping(params = "stop")
	public String stop(@RequestParam("applicationId") String applicationId, RedirectAttributes redirectAttributes)
			throws ApplicationServiceException {
		applications.stop(applicationId);
		redirectAttributes.addFlashAttribute("message", "You stopped the application " + applicationId + "!");
		return "redirect:/";
	}

	@ExceptionHandler(Exception.class)
	public String handleException(Exception e, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("message", e.getMessage());
		return "redirect:/";
	}

}
