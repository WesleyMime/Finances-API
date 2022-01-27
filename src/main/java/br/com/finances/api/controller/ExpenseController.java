package br.com.finances.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.finances.api.service.ExpenseService;
import br.com.finances.dto.ExpenseDTO;
import br.com.finances.form.ExpenseForm;
import br.com.finances.repository.ExpenseRepository;

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
	public ResponseEntity<List<ExpenseDTO>> getAllExpense(
			@RequestParam(required = false, name = "description") String description) {
		return expenseService.getAll(description);
	}	
	
	@GetMapping("{id}")
	public ResponseEntity<ExpenseDTO> getExpense(
			@PathVariable(name = "id") String id) {
		return expenseService.getOne(id);
	}
	
	@GetMapping("{ano}/{mes}")
	public ResponseEntity<List<ExpenseDTO>> getByDate(
			@PathVariable(name = "ano") String ano, @PathVariable(name = "mes") String mes) {
		return expenseService.getByDate(ano, mes);
	}

	@PostMapping
	public ResponseEntity<ExpenseDTO> postExpense(
			@Valid @RequestBody ExpenseForm expenseForm) {
		return expenseService.post(expenseForm);
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
