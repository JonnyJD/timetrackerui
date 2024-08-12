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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class TimesController {

	private final String BASE_URL;
	private final SimpleDateFormat inFormat;
	private final SimpleDateFormat outFormat;

	public TimesController(@Value("${timetracker.base-url}") String base_url) {
		this.BASE_URL = base_url;
		this.inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		this.outFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	}


	private TimeRecord toGermanFormat(TimeRecord recordIn) {
		if (recordIn == null) {
			return null;
		}
		String recordStart = recordIn.start();
		String recordEnd = recordIn.end();
		if (recordStart != null && recordEnd != null) {
			try {
				Date startDate = inFormat.parse(recordIn.start());
				Date endDate = inFormat.parse(recordIn.end());
				recordStart = outFormat.format(startDate);
				recordEnd = outFormat.format(endDate);
			} catch (ParseException e) {
				System.err.printf("Could not parse dates: %s and %s%n", recordStart, recordEnd);
			}
		}
		String recordMail = recordIn.email();
		return new TimeRecord(recordStart, recordEnd, recordMail);
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
		List<TimeRecord> fixedRecords = new ArrayList<>();
		if (records != null) {
			for (TimeRecord record: records) {
				// remove null records, which are specifically given when no records are found
				if (record != null) {
					fixedRecords.add(toGermanFormat(record));
				}
			}
		}
		model.addAttribute("records", fixedRecords);
		return "components/times_list";
	}

	@PostMapping("/times")
	public String postTimes(@RequestParam(name="email") @Valid @Email String email,
							@RequestParam(name="start") @Valid
								@DateTimeFormat(pattern = "d.M.yyyy H:m", fallbackPatterns = "yyyy-M-d H:m") Date start,
							@RequestParam(name="end") @Valid
								@DateTimeFormat(pattern = "d.M.yyyy H:m", fallbackPatterns = "yyyy-M-d H:m") Date end,
							Model model) {
		RestClient restClient = RestClient.create(BASE_URL);
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("email", email);
		formData.add("start", outFormat.format(start));
		formData.add("end", outFormat.format(end));
		TimeRecord record = restClient.post()
				.uri("/records")
				.body(formData)
				.retrieve()
				.body(TimeRecord.class);
		List<TimeRecord> records = new ArrayList<>();
		records.add(toGermanFormat(record));
		model.addAttribute("records", records);
		return "components/times_list";
	}

}
