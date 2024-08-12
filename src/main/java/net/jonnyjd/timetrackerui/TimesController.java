package net.jonnyjd.timetrackerui;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class TimesController {

	private final String BASE_URL;

	public TimesController(@Value("${timetracker.base-url}") String base_url) {
		this.BASE_URL = base_url;
	}

	@GetMapping("/times")
	public String getTimes(@RequestParam(name="email") @Valid @Email String email, Model model) {
		RestClient restClient = RestClient.create(BASE_URL);
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
	public String postTimes(@RequestParam(name="email") @Valid @Email String email,
							@RequestParam(name="start") @Valid
								@DateTimeFormat(pattern = "d.M.yyyy H:m", fallbackPatterns = "yyyy-M-d H:m") Date start,
							@RequestParam(name="end") @Valid
								@DateTimeFormat(pattern = "d.M.yyyy H:m", fallbackPatterns = "yyyy-M-d H:m") Date end,
							Model model) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		RestClient restClient = RestClient.create(BASE_URL);
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("email", email);
		formData.add("start", dateFormat.format(start));
		formData.add("end", dateFormat.format(end));
		TimeRecord record = restClient.post()
				.uri("/records")
				.body(formData)
				.retrieve()
				.body(TimeRecord.class);
		List<TimeRecord> records = new ArrayList<>();
		records.add(record);
		model.addAttribute("records", records);
		return "components/times_list";
	}

}
