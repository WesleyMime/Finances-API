package br.com.finances.api.income;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/income")
public class IncomeController {

	@Autowired
	private IncomeService incomeService;

	public IncomeController(IncomeService incomeService) {
		this.incomeService = incomeService;
	}
	
	@GetMapping
	public ResponseEntity<List<IncomeDTO>> getAllIncome(
			@RequestParam(required = false, name = "description") String description){
		return incomeService.getAll(description);
	}
	
	
	@GetMapping("{id}")
	public ResponseEntity<IncomeDTO> getIncome(
			@PathVariable(name = "id") String id) {
		return incomeService.getOne(id);
	}

	@GetMapping("{year}/{month}")
	public ResponseEntity<List<IncomeDTO>> getByDate(
			@PathVariable(name = "year") String year, @PathVariable(name = "month") String month) {
		return incomeService.getByDate(year, month);
	}
	
	@PostMapping
	public ResponseEntity<IncomeDTO> postIncome(
			@Valid @RequestBody IncomeForm incomeForm) {
		return incomeService.post(incomeForm);
	}

	@PostMapping("/list")
	public ResponseEntity<List<IncomeDTO>> postIncomeList(
			@Valid @RequestBody List<IncomeForm> incomeForms) {
		return incomeService.postList(incomeForms);
	}
	
	@PutMapping("{id}")
	public ResponseEntity<IncomeDTO> putIncome(
			@PathVariable(name = "id") String id, @Valid @RequestBody IncomeForm incomeForm) {
		return incomeService.put(id, incomeForm);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteIncome(
			@PathVariable(name = "id") String id) {
		return incomeService.delete(id);
	}
	
	
}
