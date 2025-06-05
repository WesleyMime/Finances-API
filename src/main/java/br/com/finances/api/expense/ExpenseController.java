package br.com.finances.api.expense;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
	
	@SuppressWarnings("unused")
	@Autowired
	private ExpenseRepository expenseRepository;
	
	@Autowired
	private ExpenseService expenseService;
	
	public ExpenseController(ExpenseRepository expenseRepository, ExpenseService expenseService) {
		this.expenseRepository = expenseRepository;
		this.expenseService = expenseService;
	}
	
	@GetMapping
	public ResponseEntity<List<ExpenseDTO>> getAllExpenses(
			@RequestParam(required = false, name = "description") String description) {
		return expenseService.getAll(description);
	}	
	
	@GetMapping("{id}")
	public ResponseEntity<ExpenseDTO> getExpense(
			@PathVariable(name = "id") String id) {
		return expenseService.getOne(id);
	}
	
	@GetMapping("{year}/{month}")
	public ResponseEntity<List<ExpenseDTO>> getByDate(
			@PathVariable(name = "year") String year, @PathVariable(name = "month") String month) {
		return expenseService.getByDate(year, month);
	}

	@PostMapping
	public ResponseEntity<ExpenseDTO> postExpense(
			@Valid @RequestBody ExpenseForm expenseForm) {
		return expenseService.post(expenseForm);
	}

	@PostMapping("/list")
	public ResponseEntity<List<ExpenseDTO>> postExpenseList(
			@Valid @RequestBody List<ExpenseForm> expenseForms) {
		return expenseService.postList(expenseForms);
	}
	
	@PutMapping("{id}")
	public ResponseEntity<ExpenseDTO> putExpense(
			@PathVariable(name = "id") String id, @Valid @RequestBody ExpenseForm expenseForm) {
		return expenseService.put(id, expenseForm);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteExpense(
			@PathVariable(name = "id") String id) {
		return expenseService.delete(id);
	}
	
	
}
