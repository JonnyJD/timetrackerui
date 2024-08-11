package net.jonnyjd.timetrackerui;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TimesController {

	@GetMapping("/times")
	public String getTimes(@RequestParam(name="email") @Valid @Email String email, Model model) {
		RestClient restClient = RestClient.create("http://localhost:8080/");
		String url = UriComponentsBuilder.fromPath("/records")
				.queryParam("email", email)
				.toUriString();
		List<TimeRecord> records = restClient.get()
				.uri(url)
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});
		// remove null records, which are specifically given when no records are found
		List<TimeRecord> nonNullRecords = new ArrayList<>();
		for (TimeRecord record: records) {
			if (record != null) {
				nonNullRecords.add(record);
			}
		}
		model.addAttribute("records", nonNullRecords);
		return "components/times_list";
	}

	@PostMapping("/times")
	public ResponseEntity<String> postTimes(@RequestParam(name="email") String email,
											@RequestParam(name="start") String start,
											@RequestParam(name="end") String end) {
		return new ResponseEntity<>("not implemented", HttpStatus.NOT_IMPLEMENTED);
	}

}
