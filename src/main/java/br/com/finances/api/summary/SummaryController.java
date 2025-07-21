package br.com.finances.api.summary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/summary")
public class SummaryController {

	private final SummaryService summaryService;

	public SummaryController(SummaryService summaryService) {
		this.summaryService = summaryService;
	}

	@GetMapping("/{year}/{month}")
	public ResponseEntity<SummaryDTO> getSummaryByDate(
			@PathVariable(name = "year") String year, @PathVariable(name = "month") String month) {
		Optional<SummaryDTO> summaryByDate = summaryService.getSummaryByDate(year, month);
		return summaryByDate
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.badRequest().build());
	}

	@GetMapping("/last-year")
	public ResponseEntity<SummaryLastYearDTO> getSummaryOfLastYear(Principal principal) {
		return ResponseEntity.ok(summaryService.getSummaryOfLastYear(LocalDate.now(), principal));
	}
}
