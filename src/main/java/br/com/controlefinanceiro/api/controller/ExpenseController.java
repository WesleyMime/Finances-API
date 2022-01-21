package br.com.controlefinanceiro.api.controller;

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
import org.springframework.web.bind.annotation.RestController;

import br.com.controlefinanceiro.api.service.ExpenseService;
import br.com.controlefinanceiro.form.ExpenseForm;
import br.com.controlefinanceiro.model.Expense;
import br.com.controlefinanceiro.repository.ExpenseRepository;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
	
	@Autowired
	private ExpenseRepository expenseRepository;
	
	@Autowired
	private ExpenseService expenseService;
	
	public ExpenseController(ExpenseRepository expenseRepository, ExpenseService expenseService) {
		this.expenseRepository = expenseRepository;
		this.expenseService = expenseService;
	}
	
	@GetMapping
	public ResponseEntity<List<Expense>> getAllExpense(){
		return expenseService.getAll();
	}
	
	@GetMapping("{id}")
	public ResponseEntity<Expense> getExpense(@PathVariable(name = "id") String id) {
		return expenseService.getOne(id);
	}

	@PostMapping
	public ResponseEntity<Expense> postExpense(@Valid @RequestBody ExpenseForm expenseForm) {
		return expenseService.post(expenseForm);
	}
	
	@PutMapping("{id}")
	public ResponseEntity<Expense> putExpense(@PathVariable(name = "id") String id, @Valid @RequestBody ExpenseForm expenseForm) {
		return expenseService.put(id, expenseForm);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteExpense(@PathVariable(name = "id") String id) {
		return expenseService.delete(id);
	}
	
	
}
