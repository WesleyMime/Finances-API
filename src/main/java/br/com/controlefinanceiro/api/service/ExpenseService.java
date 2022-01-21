package br.com.controlefinanceiro.api.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.controlefinanceiro.form.ExpenseForm;
import br.com.controlefinanceiro.model.Expense;
import br.com.controlefinanceiro.repository.ExpenseRepository;

@Service
public class ExpenseService {
	
	@Autowired
	private ExpenseRepository expenseRepository;
	
	public ExpenseService(ExpenseRepository expenseRepository) {
		this.expenseRepository = expenseRepository;
	}

	public ResponseEntity<List<Expense>> getAll() {
		List<Expense> listIncome = expenseRepository.findAll();
		return ResponseEntity.ok(listIncome);
	}

	public ResponseEntity<Expense> getOne(String id) {
		Optional<Expense> incomeOptional = null;
		
		try {
			incomeOptional = expenseRepository.findById(Long.parseLong(id));
		} catch(NumberFormatException e) {
			// Not a number
			return ResponseEntity.badRequest().build();
		}
		
		Expense expense = null;
		
		try {
			expense = incomeOptional.get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(expense);
	}

	public ResponseEntity<Expense> post(Expense expense) {
		Expense save = expenseRepository.save(expense);
		return ResponseEntity.status(HttpStatus.CREATED).body(save);
	}
	
	public ResponseEntity<Expense> put(String id, ExpenseForm form) {
		
		ResponseEntity<Expense> one = getOne(id);
		if(one.getBody() == null) {
			// Return Bad Request or Not Found
			return one;
		}
		Expense updated = form.update(one.getBody());
		expenseRepository.save(updated);
		return ResponseEntity.ok(updated);
	}

	public ResponseEntity<?> delete(String id) {
		ResponseEntity<Expense> one = getOne(id);
		if(one.getBody() == null) {
			// Return Bad Request or Not Found
			return one;
		}
		expenseRepository.deleteById(Long.parseLong(id));
		return ResponseEntity.ok().build();
	}
}
