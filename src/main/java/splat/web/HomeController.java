package splat.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import splat.core.Platform;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {

	private final Platform platform;

	@GetMapping
	public String get(Model model) {

		model.addAttribute("applications", platform.getAllApplications());

		return "index";
	}

	@PostMapping(params = "delete")
	public String delete(@RequestParam("appName") String appName, RedirectAttributes redirectAttributes) {

		try {
			platform.delete(appName);
			redirectAttributes.addFlashAttribute("message", "You deleted the application " + appName + "!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "The application " + appName + " couldn't be deleted!");
		}

		return "redirect:/";

	}
	
	@PostMapping(params = "restart")
	public String restart(@RequestParam("appName") String appName, RedirectAttributes redirectAttributes) {

		try {
			platform.restart(appName);
			redirectAttributes.addFlashAttribute("message", "You restarted the application " + appName + "!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "The application " + appName + " couldn't be restarted!");
		}

		return "redirect:/";

	}
	
	@PostMapping(params = "stop")
	public String stop(@RequestParam("appName") String appName, RedirectAttributes redirectAttributes) {

		try {
			platform.stop(appName);
			redirectAttributes.addFlashAttribute("message", "You stopped the application " + appName + "!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "The application " + appName + " couldn't be stopped!");
		}

		return "redirect:/";

	}

}
