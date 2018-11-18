package splat.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import splat.core.LocationService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

	private final LocationService locationService;

	@ModelAttribute("adminForm")
	public AdminForm adminForm() {
		AdminForm adminForm = new AdminForm();
		adminForm.setLocation(locationService.getLocation());
		return adminForm;
	}

	@GetMapping
	public String get(Model model) {
		return "admin";
	}

	@PostMapping
	public String update(RedirectAttributes redirectAttributes, @ModelAttribute("adminForm") AdminForm form,
			BindingResult bindingResult) {

		return "redirect:/";
	}

}
