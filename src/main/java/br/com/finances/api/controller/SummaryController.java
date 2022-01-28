package br.com.finances.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.finances.api.service.SummaryService;
import br.com.finances.dto.SummaryDTO;

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
}
