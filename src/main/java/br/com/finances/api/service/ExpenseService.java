package br.com.finances.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.finances.dto.ExpenseDTO;
import br.com.finances.form.ExpenseForm;
import br.com.finances.model.Expense;
import br.com.finances.repository.ExpenseRepository;

@Service
public class ExpenseService {
	
	@Autowired
	private ExpenseRepository expenseRepository;
	
	public ExpenseService(ExpenseRepository expenseRepository) {
		this.expenseRepository = expenseRepository;
	}

	public ResponseEntity<List<ExpenseDTO>> getAll(String description) {
		List<ExpenseDTO> listExpenseDto = new ArrayList<>();
		List<Expense> listExpense = new ArrayList<>();
		
		if(description == null) {
			listExpense = expenseRepository.findAll();
		} else {
			Optional<Expense> optionalExpense = expenseRepository.findByDescription(description);
			
			try {
				Expense expense = optionalExpense.get();
				listExpense.add(expense);
			} catch(NoSuchElementException e) {
				return ResponseEntity.notFound().build();
			}
		}
		
		listExpense.forEach(i -> {
			listExpenseDto.add(new ExpenseDTO(i));
		});
		return ResponseEntity.ok(listExpenseDto);
	}

	public ResponseEntity<ExpenseDTO> getOne(String id) {
		ResponseEntity<ExpenseDTO> expenseDto = tryToGetById(id);
		return expenseDto;
	}
	
	public ResponseEntity<List<ExpenseDTO>> getByDate(String yearString, String monthString) {
		Integer year;
		Integer month;
		
		try {
			year = Integer.parseInt(yearString);
			month = Integer.parseInt(monthString);
		} catch(NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}		
		
		List<Expense> listExpense = expenseRepository.findByYearAndMonth(year, month);
		if(listExpense.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		List<ExpenseDTO> listExpenseDto = new ArrayList<>();
		
		listExpense.forEach(e -> {
			listExpenseDto.add(new ExpenseDTO(e));
		});
		
		return ResponseEntity.ok(listExpenseDto);
	}

	public ResponseEntity<ExpenseDTO> post(ExpenseForm form) {
		Expense expense = form.converter();
		
		checkIfAlreadyExists(expense);
		
		Expense save = expenseRepository.save(expense);
		
		ExpenseDTO expenseDto = new ExpenseDTO(save);
		return ResponseEntity.status(HttpStatus.CREATED).body(expenseDto);
	}
	
	public ResponseEntity<ExpenseDTO> put(String id, ExpenseForm form) {
		
		// Try to find by id
		ResponseEntity<ExpenseDTO> getById = tryToGetById(id);
		if(getById.hasBody() == false) {
			return getById;
		}
		
		Expense expense = expenseRepository.getById(Long.parseLong(id));		
		Expense updated = form.update(expense);
		
		checkIfAlreadyExists(updated);
		
		Expense save = expenseRepository.save(updated);		
		ExpenseDTO expenseDto = new ExpenseDTO(save);		
		return ResponseEntity.ok(expenseDto);
	}

	public ResponseEntity<?> delete(String id) {
		ResponseEntity<ExpenseDTO> getById = tryToGetById(id);
		if(getById.hasBody() == false) {
			return getById;
		}
		expenseRepository.deleteById(Long.parseLong(id));
		return ResponseEntity.ok().build();
	}
	
	private ResponseEntity<ExpenseDTO> tryToGetById(String id) {
		Optional<Expense> expenseOptional = null;
		
		try {
			expenseOptional = expenseRepository.findById(Long.parseLong(id));
		} catch(NumberFormatException e) {
			// Not a number
			return ResponseEntity.badRequest().build();
		}		
		Expense expense = null;
		
		try {
			expense = expenseOptional.get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
		ExpenseDTO expenseDTO = new ExpenseDTO(expense);
		
		return ResponseEntity.ok(expenseDTO);
	}
	
	private void checkIfAlreadyExists(Expense expense) {
		String description = expense.getDescription();
		Integer monthNumber = expense.getDate().getMonthValue();
		
		Optional<Expense> sameExpense = expenseRepository.findByDescriptionAndMonth(description, monthNumber);
		if(sameExpense.isPresent()) {
			throw new EntityExistsException();
		}
	}
}
