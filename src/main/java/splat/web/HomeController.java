package splat.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import splat.core.ApplicationService;
import splat.core.Platform;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {
	
	private final Platform platform; 

	@GetMapping
	public String get(Model model) {
		
		model.addAttribute("applications", platform.findAllApplications());
		
		return "index";
	}

}
