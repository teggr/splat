package splat.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import splat.core.Platform;
import splat.core.PlatformException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {

	private final Platform platform;

	@GetMapping
	public String get(Model model) {
		try {
			model.addAttribute("applications", platform.getAllApplications());
		} catch (PlatformException e) {
			model.addAttribute("message", e.getMessage());
		}
		return "index";
	}

	@PostMapping(params = "delete")
	public String delete(@RequestParam("appName") String appName, RedirectAttributes redirectAttributes)
			throws PlatformException {
		platform.delete(appName);
		redirectAttributes.addFlashAttribute("message", "You deleted the application " + appName + "!");
		return "redirect:/";
	}

	@PostMapping(params = "restart")
	public String restart(@RequestParam("appName") String appName, RedirectAttributes redirectAttributes)
			throws PlatformException {
		platform.restart(appName);
		redirectAttributes.addFlashAttribute("message", "You restarted the application " + appName + "!");
		return "redirect:/";
	}

	@PostMapping(params = "stop")
	public String stop(@RequestParam("appName") String appName, RedirectAttributes redirectAttributes)
			throws PlatformException {
		platform.stop(appName);
		redirectAttributes.addFlashAttribute("message", "You stopped the application " + appName + "!");
		return "redirect:/";
	}

	@ExceptionHandler(Exception.class)
	public String handleException(Exception e, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("message", e.getMessage());
		return "redirect:/";
	}

}
