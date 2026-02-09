package br.com.finances.api.expense;

import br.com.finances.api.generic.ScrollDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

	private final ExpenseService expenseService;

	public ExpenseController(ExpenseService expenseService) {
		this.expenseService = expenseService;
	}
	
	@GetMapping
	public ResponseEntity<ScrollDTO<ExpenseDTO>> getAllExpenses(
			@RequestParam(required = false, name = "description") String description,
			@RequestParam(required = false, name = "lastId") Integer lastId,
			@RequestParam(required = false, name = "lastDate") LocalDate lastDate,
			Principal principal) {
		ScrollDTO<ExpenseDTO> scrollDTO = expenseService.getAll(description, lastId, lastDate, principal);
		return scrollDTO.getData().isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(scrollDTO);
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
			@Valid @RequestBody ExpenseForm expenseForm, Principal principal) {
		return expenseService.post(expenseForm, principal);
	}

	@PostMapping("/list")
	public ResponseEntity<List<ExpenseDTO>> postExpenseList(
			@Valid @RequestBody List<ExpenseForm> expenseForms, Principal principal) {
		return expenseService.postList(expenseForms, principal);
	}

	@PutMapping("{id}")
	public ResponseEntity<ExpenseDTO> putExpense(
			@PathVariable(name = "id") String id, @Valid @RequestBody ExpenseForm expenseForm, Principal principal) {
		return expenseService.put(id, expenseForm, principal);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<ExpenseDTO> deleteExpense(@PathVariable(name = "id") String id, Principal principal) {
		return expenseService.delete(id, principal);
	}
	
	
}
