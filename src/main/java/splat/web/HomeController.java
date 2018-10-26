package splat.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {
	
	private final ApplicationService applicationService; 

	@GetMapping
	public String get(Model model) {
		
		model.addAttribute("applications", applicationService.findAll());
		
		return "index";
	}

}
