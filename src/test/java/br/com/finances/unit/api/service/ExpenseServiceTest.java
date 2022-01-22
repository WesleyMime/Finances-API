package br.com.finances.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.finances.api.service.ExpenseService;
import br.com.finances.dto.ExpenseDTO;
import br.com.finances.form.ExpenseForm;
import br.com.finances.model.Expense;
import br.com.finances.repository.ExpenseRepository;

class ExpenseServiceTest {

	@Mock
	private ExpenseRepository expenseRepository;
	
	private ExpenseService expenseService;
	
	private List<Expense> listExpense = generateExpense();
	
	private ExpenseForm expenseForm = new ExpenseForm("expenseForm description", new BigDecimal("500"), LocalDate.now());
	private ExpenseDTO expenseDto = new ExpenseDTO(listExpense.get(2));
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		
		expenseService = new ExpenseService(expenseRepository);
		
		Mockito.when(expenseRepository.findAll())
		.thenReturn(listExpense);
		
		Optional<Expense> optional = Optional.of(listExpense.get(0));
		Mockito.when(expenseRepository.findById(1l))
		.thenReturn(optional);
		
		Mockito.when(expenseRepository.getById(1l))
		.thenReturn(listExpense.get(0));
		
		Mockito.when(expenseRepository.save(Mockito.any()))
		.thenReturn(listExpense.get(0));
	}
	
	//GET
	
	@Test
	void shouldReturnAllIncome() {
		ResponseEntity<List<ExpenseDTO>> all = expenseService.getAll();
		assertEquals(expenseDto.getDescription(), all.getBody().get(2).getDescription());;
	}
	
	@Test
	void shouldReturnIncomeById() {
		ResponseEntity<ExpenseDTO> income = expenseService.getOne("1");
		assertEquals(HttpStatus.OK, income.getStatusCode());;
	}
	
	@Test
	void shouldNotReturnIncomeById() {
		ResponseEntity<ExpenseDTO> income = expenseService.getOne("a");
		assertEquals(HttpStatus.BAD_REQUEST, income.getStatusCode());;
	}
	
	@Test
	void shouldNotFindIncomeById() {
		ResponseEntity<ExpenseDTO> income = expenseService.getOne("186767587");
		assertEquals(HttpStatus.NOT_FOUND, income.getStatusCode());;
	}
	
	//POST
	
	@Test
	void shouldPostIncome() {
		ResponseEntity<ExpenseDTO> post = expenseService.post(expenseForm);
		assertEquals(HttpStatus.CREATED, post.getStatusCode());
	}
	
	//UPDATE
	@Test
	void shouldUpdateIncome() {
		ResponseEntity<ExpenseDTO> newIncome = expenseService.put("1", expenseForm);
		assertEquals(HttpStatus.OK, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotFindIncomeToUpdate() {
		ResponseEntity<ExpenseDTO> newIncome = expenseService.put("100000000", expenseForm);
		assertEquals(HttpStatus.NOT_FOUND, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotUpdateIncome() {
		ResponseEntity<ExpenseDTO> newIncome = expenseService.put("a", expenseForm);
		assertEquals(HttpStatus.BAD_REQUEST, newIncome.getStatusCode());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteIncome() {
		ResponseEntity<?> delete = expenseService.delete("1");
		assertEquals(HttpStatus.OK, delete.getStatusCode());
	}
	
	@Test
	void shouldNotFindIncomeToDelete() {
		ResponseEntity<?> delete = expenseService.delete("100000");
		assertEquals(HttpStatus.NOT_FOUND, delete.getStatusCode());
	}
	
	@Test
	void shouldNotDeleteIncome() {
		ResponseEntity<?> delete = expenseService.delete("a");
		assertEquals(HttpStatus.BAD_REQUEST, delete.getStatusCode());
	}
	
	private List<Expense> generateExpense() {
		List<Expense> listExpense = new ArrayList<>();
		listExpense.add(new Expense("description expense test", new BigDecimal("1500"), LocalDate.now()));
		listExpense.add(new Expense("description test expense", new BigDecimal("2500"), LocalDate.now()));
		listExpense.add(new Expense("test expense description", new BigDecimal("3500"), LocalDate.now()));
		return listExpense;
	}
	
}
