package br.com.finances.api.income;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/income")
public class IncomeController {
	
	@SuppressWarnings("unused")
	@Autowired
	private IncomeRepository incomeRepository;
	
	@Autowired
	private IncomeService incomeService;
	
	public IncomeController(IncomeService incomeService, IncomeRepository incomeRepository) {
		this.incomeService = incomeService;
		this.incomeRepository = incomeRepository;
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
