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

import br.com.controlefinanceiro.api.service.IncomeService;
import br.com.controlefinanceiro.form.IncomeForm;
import br.com.controlefinanceiro.model.Income;
import br.com.controlefinanceiro.repository.IncomeRepository;

@RestController
@RequestMapping("/income")
public class IncomeController {
	
	@Autowired
	private IncomeRepository incomeRepository;
	
	@Autowired
	private IncomeService incomeService;
	
	public IncomeController(IncomeRepository incomeRepository, IncomeService incomeService) {
		this.incomeRepository = incomeRepository;
		this.incomeService = incomeService;
	}
	
	@GetMapping
	public ResponseEntity<List<Income>> getAllIncome(){
		return incomeService.getAll();
	}
	
	@GetMapping("{id}")
	public ResponseEntity<Income> getIncome(@PathVariable(name = "id") String id) {
		return incomeService.getOne(id);
	}

	@PostMapping
	public ResponseEntity<Income> postIncome(@Valid @RequestBody IncomeForm incomeForm) {
		return incomeService.post(incomeForm);
	}
	
	@PutMapping("{id}")
	public ResponseEntity<Income> putIncome(@PathVariable(name = "id") String id, @Valid @RequestBody IncomeForm incomeForm) {
		return incomeService.put(id, incomeForm);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteIncome(@PathVariable(name = "id") String id) {
		return incomeService.delete(id);
	}
	
	
}
