package br.com.finances.api.summary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<SummaryDTO> getSummaryByMonth(
			@PathVariable(name = "year") String year, @PathVariable(name = "month") String month,
			Principal principal) {
		Optional<SummaryDTO> summaryByDate = summaryService.getSummaryByMonth(year, month, principal);
		return summaryByDate
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.badRequest().build());
	}

	@GetMapping("/last-year")
	public ResponseEntity<SummaryLastYearDTO> getSummaryOfLastYear(Principal principal) {
		return ResponseEntity.ok(summaryService.getSummaryOfLastYear(LocalDate.now(), principal));
	}

	@GetMapping
	public ResponseEntity<SummaryBasicDTO> getSummaryByDate(
			@RequestParam(name = "yearFrom") String yearFrom,
			@RequestParam(name = "monthFrom") String monthFrom,
			@RequestParam(name = "yearTo") String yearTo,
			@RequestParam(name = "monthTo") String monthTo,
			Principal principal) {
		Optional<SummaryBasicDTO> summaryByYear = summaryService.getSummaryByDate(yearFrom, monthFrom,
				yearTo, monthTo, principal);
		return summaryByYear
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.badRequest().build());
	}

	@GetMapping("/account")
	public ResponseEntity<SummaryBasicDTO> getAccountSummary(Principal principal) {
		return ResponseEntity.ok(summaryService.getAccountSummary(LocalDate.now(), principal));
	}
}
