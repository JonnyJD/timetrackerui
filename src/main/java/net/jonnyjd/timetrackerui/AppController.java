package net.jonnyjd.timetrackerui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class AppController {

	@GetMapping("/")
	public String get(Model model) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		model.addAttribute("now", dateFormat.format(new Date()));
		return "index";
	}

}
