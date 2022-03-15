package br.com.finances.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.Category;
import br.com.finances.api.expense.Expense;
import br.com.finances.api.expense.ExpenseDTO;
import br.com.finances.api.expense.ExpenseDtoMapper;
import br.com.finances.api.expense.ExpenseForm;
import br.com.finances.api.expense.ExpenseFormMapper;
import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.expense.ExpenseService;

class ExpenseServiceTest {

	@Mock
	private ExpenseRepository expenseRepository;
	@Mock
	private ClientRepository clientRepository;
	
	private ExpenseDtoMapper dtoMapper = new ExpenseDtoMapper();
	
	private ExpenseFormMapper formMapper = new ExpenseFormMapper();
	@Mock
	private Principal principal;	
	
	private ExpenseService expenseService;
	
	private static final String DESCRIPTION = "Description";
	private static final BigDecimal VALUE = new BigDecimal("1500");
	private static final LocalDate DATE = LocalDate.of(2022, 01, 01);
	private static final Client CLIENT = SecurityContextFactory.setClient();
	
	private static final Expense EXPENSE = new Expense(DESCRIPTION, VALUE, DATE, Category.Others);
	private static final ExpenseForm FORM = new ExpenseForm(DESCRIPTION, VALUE, DATE, Category.Others);
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		SecurityContextFactory.setClient();
		
		this.expenseService = new ExpenseService(expenseRepository, clientRepository, dtoMapper, formMapper);
		
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
		assertEquals(EXPENSE.getDescription(), all.getBody().get(0).getDescription());
	}
	
	@Test
	void shouldReturnExpenseByDescription() {
		when(expenseRepository.findByDescriptionAndClient(DESCRIPTION, CLIENT))
		.thenReturn(Optional.of(EXPENSE));
		ResponseEntity<List<ExpenseDTO>> all = expenseService.getAll(DESCRIPTION);
		assertEquals(EXPENSE.getDescription(),  all.getBody().get(0).getDescription());
	}
	
	@Test
	void shouldNotFindExpenseByDescription() {
		ResponseEntity<List<ExpenseDTO>> all = expenseService.getAll("a");
		assertEquals(HttpStatus.NOT_FOUND, all.getStatusCode());
	}
	
	@Test
	void shouldReturnExpenseById() {
		when(expenseRepository.findByIdAndClient(1l, CLIENT))
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
		when(expenseRepository.findByIdAndClient(1l, CLIENT))
		.thenReturn(Optional.of(EXPENSE));
		when(expenseRepository.getById(any()))
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
		when(expenseRepository.findByIdAndClient(1l, CLIENT))
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
