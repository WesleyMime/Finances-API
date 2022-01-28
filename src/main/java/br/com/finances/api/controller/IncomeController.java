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

import br.com.finances.api.service.IncomeService;
import br.com.finances.dto.IncomeDTO;
import br.com.finances.form.IncomeForm;
import br.com.finances.repository.IncomeRepository;

@RestController
@RequestMapping("/income")
public class IncomeController {
	
	@SuppressWarnings("unused")
	@Autowired
	private IncomeRepository incomeRepository;
	
	@Autowired
	private IncomeService incomeService;
	
	public IncomeController(IncomeRepository incomeRepository, IncomeService incomeService) {
		this.incomeRepository = incomeRepository;
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
