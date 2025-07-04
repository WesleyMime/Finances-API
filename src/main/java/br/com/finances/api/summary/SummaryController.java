package br.com.finances.api.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/summary")
public class SummaryController {

	@Autowired
	private SummaryService summaryService;
	
	@GetMapping("/{year}/{month}")
	public ResponseEntity<SummaryDTO> getSummaryByDate(
			@PathVariable(name = "year") String year, @PathVariable(name = "month") String month) {
		return summaryService.getSummaryByDate(year, month);
	}

	@GetMapping("/last-year")
	public ResponseEntity<SummaryLastYearDTO> getSummaryOfLastYear() {
		return ResponseEntity.ok(summaryService.getSummaryOfLastYear(LocalDate.now()));
	}
}
