package br.com.finances.unit.api.service;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ExpenseServiceTest {

	private final ExpenseRepository expenseRepository;
	private final ClientRepository clientRepository;
	private final ExpenseService expenseService;

	public ExpenseServiceTest() {
		this.expenseRepository = Mockito.mock(ExpenseRepository.class);
		this.clientRepository = Mockito.mock(ClientRepository.class);
		ExpenseDtoMapper dtoMapper = new ExpenseDtoMapper();
		ExpenseFormMapper formMapper = new ExpenseFormMapper();
		this.expenseService = new ExpenseService(expenseRepository, clientRepository, dtoMapper,
				formMapper);
	}

	private static final String DESCRIPTION = "Description";
	private static final BigDecimal VALUE = new BigDecimal("1500");
	private static final LocalDate DATE = LocalDate.of(2022, 1, 1);
	private static final Client CLIENT = SecurityContextFactory.setClient();
	
	private static final Expense EXPENSE = new Expense(DESCRIPTION, VALUE, DATE, Category.Others);
	private static final ExpenseForm FORM = new ExpenseForm(DESCRIPTION, VALUE, DATE, Category.Others);
	
	@BeforeEach
	void beforeEach() {
		when(clientRepository.findByEmail(anyString()))
				.thenReturn(Optional.of(CLIENT));
		when(expenseRepository.save(any()))
		.thenReturn(EXPENSE);
	}
	
	//GET
	
	@Test
	void shouldReturnAllExpense() {
		when(expenseRepository.findByClient(CLIENT))
		.thenReturn(List.of(EXPENSE));
		ResponseEntity<List<ExpenseDTO>> all = expenseService.getAll(null);
		Assertions.assertNotNull(all.getBody());
		assertEquals(EXPENSE.getDescription(), all.getBody().getFirst().getDescription());
	}
	
	@Test
	void shouldReturnExpenseByDescription() {
		when(expenseRepository.findByDescriptionAndClient(DESCRIPTION, CLIENT))
		.thenReturn(Optional.of(EXPENSE));
		ResponseEntity<List<ExpenseDTO>> all = expenseService.getAll(DESCRIPTION);
		Assertions.assertNotNull(all.getBody());
		assertEquals(EXPENSE.getDescription(), all.getBody().getFirst().getDescription());
	}
	
	@Test
	void shouldNotFindExpenseByDescription() {
		ResponseEntity<List<ExpenseDTO>> all = expenseService.getAll("a");
		assertEquals(HttpStatus.NOT_FOUND, all.getStatusCode());
	}
	
	@Test
	void shouldReturnExpenseById() {
		when(expenseRepository.findByIdAndClient(1L, CLIENT))
		.thenReturn(Optional.of(EXPENSE));
		ResponseEntity<ExpenseDTO> income = expenseService.getOne("1");
		assertEquals(HttpStatus.OK, income.getStatusCode());;
	}
	
	@Test
	void shouldNotReturnExpenseById() {
		ResponseEntity<ExpenseDTO> income = expenseService.getOne("a");
		assertEquals(HttpStatus.BAD_REQUEST, income.getStatusCode());
	}
	
	@Test
	void shouldNotFindExpenseById() {
		ResponseEntity<ExpenseDTO> income = expenseService.getOne("186767587");
		assertEquals(HttpStatus.NOT_FOUND, income.getStatusCode());
	}
	
	@Test
	void shouldReturnExpenseByDate() {
		when(expenseRepository.findByYearAndMonth(anyInt(), anyInt(), any()))
		.thenReturn(List.of(EXPENSE));
		
		String yearString = String.valueOf(DATE.getYear());
		String monthString = String.valueOf(DATE.getMonthValue());
		
		ResponseEntity<List<ExpenseDTO>> expenseByDate = expenseService.getByDate(yearString, monthString);
		assertEquals(HttpStatus.OK, expenseByDate.getStatusCode());
	}
	
	@Test
	void shouldNotReturnExpenseByDate() {
		
		ResponseEntity<List<ExpenseDTO>> expenseByDate = expenseService.getByDate("a", "a");
		assertEquals(HttpStatus.BAD_REQUEST, expenseByDate.getStatusCode());
	}
	
	@Test
	void shouldNotFindExpenseByDate() {		
		ResponseEntity<List<ExpenseDTO>> expenseByDate = expenseService.getByDate("1000", "01");
		assertEquals(HttpStatus.NOT_FOUND, expenseByDate.getStatusCode());
	}
	
	//POST
	
	@Test
	void shouldPostExpense() {
		when(expenseRepository.save(any()))
		.thenReturn(EXPENSE);
		ResponseEntity<ExpenseDTO> post = expenseService.post(FORM);
		assertEquals(HttpStatus.CREATED, post.getStatusCode());
	}
	
	//UPDATE
	@Test
	void shouldUpdateExpense() {
		when(expenseRepository.findByIdAndClient(1L, CLIENT))
		.thenReturn(Optional.of(EXPENSE));
		when(expenseRepository.getReferenceById(any()))
		.thenReturn(EXPENSE);
		ResponseEntity<ExpenseDTO> newIncome = expenseService.put("1", FORM);
		assertEquals(HttpStatus.OK, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotFindExpenseToUpdate() {
		ResponseEntity<ExpenseDTO> newIncome = expenseService.put("100000000", FORM);
		assertEquals(HttpStatus.NOT_FOUND, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotUpdateExpense() {
		ResponseEntity<ExpenseDTO> newIncome = expenseService.put("a", FORM);
		assertEquals(HttpStatus.BAD_REQUEST, newIncome.getStatusCode());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteExpense() {
		when(expenseRepository.findByIdAndClient(1L, CLIENT))
		.thenReturn(Optional.of(EXPENSE));
		ResponseEntity<?> delete = expenseService.delete("1");
		assertEquals(HttpStatus.OK, delete.getStatusCode());
	}
	
	@Test
	void shouldNotFindExpenseToDelete() {
		ResponseEntity<?> delete = expenseService.delete("100000");
		assertEquals(HttpStatus.NOT_FOUND, delete.getStatusCode());
	}
	
	@Test
	void shouldNotDeleteExpense() {
		ResponseEntity<?> delete = expenseService.delete("a");
		assertEquals(HttpStatus.BAD_REQUEST, delete.getStatusCode());
	}
	
}
