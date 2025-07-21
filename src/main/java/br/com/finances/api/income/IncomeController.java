package br.com.finances.api.income;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/income")
public class IncomeController {

	private final IncomeService incomeService;

	public IncomeController(IncomeService incomeService) {
		this.incomeService = incomeService;
	}

	@GetMapping
	public ResponseEntity<List<IncomeDTO>> getAllIncome(
			@RequestParam(required = false, name = "description") String description) {
		List<IncomeDTO> all = incomeService.getAll(description);
		return all.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(all);
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
			@Valid @RequestBody IncomeForm incomeForm, Principal principal) {
		return incomeService.post(incomeForm, principal);
	}

	@PostMapping("/list")
	public ResponseEntity<List<IncomeDTO>> postIncomeList(
			@Valid @RequestBody List<IncomeForm> incomeForms, Principal principal) {
		return incomeService.postList(incomeForms, principal);
	}

	@PutMapping("{id}")
	public ResponseEntity<IncomeDTO> putIncome(
			@PathVariable(name = "id") String id, @Valid @RequestBody IncomeForm incomeForm, Principal principal) {
		return incomeService.put(id, incomeForm, principal);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<IncomeDTO> deleteIncome(
			@PathVariable(name = "id") String id, Principal principal) {
		return incomeService.delete(id, principal);
	}


}
