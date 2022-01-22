package br.com.finances.api.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.finances.form.IncomeForm;
import br.com.finances.model.Income;
import br.com.finances.repository.IncomeRepository;

@Service
public class IncomeService {
	
	@Autowired
	private IncomeRepository incomeRepository;
	
	public IncomeService(IncomeRepository incomeRepository) {
		this.incomeRepository = incomeRepository;
	}

	public ResponseEntity<List<Income>> getAll() {
		List<Income> listIncome = incomeRepository.findAll();
		return ResponseEntity.ok(listIncome);
	}

	public ResponseEntity<Income> getOne(String id) {
		Optional<Income> incomeOptional = null;
		
		try {
			incomeOptional = incomeRepository.findById(Long.parseLong(id));
		} catch(NumberFormatException e) {
			// Not a number
			return ResponseEntity.badRequest().build();
		}
		
		Income income = null;
		
		try {
			income = incomeOptional.get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(income);
	}

	public ResponseEntity<Income> post(IncomeForm form) {
		Income income = form.converter();
		
		Optional<Income> sameIncome = checkIfAlreadyExists(income);
		
		if(sameIncome.isPresent()) {
			return ResponseEntity.badRequest().build();
		}
		
		Income save = incomeRepository.save(income);
		return ResponseEntity.status(HttpStatus.CREATED).body(save);
	}
	
	public ResponseEntity<Income> put(String id, IncomeForm form) {
		
		// Try to find by id
		ResponseEntity<Income> one = getOne(id);
		if(one.getBody() == null) {
			// Return Bad Request or Not Found
			return one;
		}		
		Income updated = form.update(one.getBody());
		
		Optional<Income> sameIncome = checkIfAlreadyExists(updated);		
		if(sameIncome.isPresent()) {
			return ResponseEntity.badRequest().build();
		}
		
		incomeRepository.save(updated);
		return ResponseEntity.ok(updated);
	}

	public ResponseEntity<?> delete(String id) {
		ResponseEntity<Income> one = getOne(id);
		if(one.getBody() == null) {
			// Return Bad Request or Not Found
			return one;
		}
		incomeRepository.deleteById(Long.parseLong(id));
		return ResponseEntity.ok().build();
	}
	
	private Optional<Income> checkIfAlreadyExists(Income income) {		
		String description = income.getDescription();
		Integer monthNumber = income.getDate().getMonthValue();
		
		Optional<Income> sameIncome = incomeRepository.findByDescriptionAndMonth(description, monthNumber);

		return sameIncome;
	}
}
