package br.com.finances.unit.api.service;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.*;
import br.com.finances.api.generic.ScrollDTO;
import br.com.finances.config.CacheEvictionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.ScrollPosition.keyset;

class ExpenseServiceTest {

	private final ExpenseRepository expenseRepository;
	private final ClientRepository clientRepository;
	private final ExpenseService expenseService;

	public ExpenseServiceTest() {
		this.expenseRepository = Mockito.mock(ExpenseRepository.class);
		this.clientRepository = Mockito.mock(ClientRepository.class);
		CacheEvictionService cacheEvictionService = Mockito.mock(CacheEvictionService.class);
		ExpenseDtoMapper dtoMapper = new ExpenseDtoMapper();
		ExpenseFormMapper formMapper = new ExpenseFormMapper();
		this.expenseService = new ExpenseService(expenseRepository, clientRepository, dtoMapper,
				formMapper, cacheEvictionService);
	}

	private static final String DESCRIPTION = "Description";
	private static final BigDecimal VALUE = new BigDecimal("1500");
	private static final LocalDate DATE = LocalDate.of(2022, 1, 1);
	private static final Client CLIENT = SecurityContextFactory.setClient();
	private static final Principal PRINCIPAL = SecurityContextHolder.getContext().getAuthentication();

	private static final Expense EXPENSE = new Expense(DESCRIPTION, VALUE, DATE, Category.OTHERS);
	private static final ExpenseForm FORM = new ExpenseForm(DESCRIPTION, VALUE, DATE, Category.OTHERS.toString());
	
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
		List<Expense> mockData = List.of(EXPENSE);

		when(expenseRepository.findFirst10ByClientOrderByDateDesc(CLIENT, keyset())).thenReturn(
				Window.from(mockData, new KeysetScrollPositionProvider()));

		ScrollDTO<ExpenseDTO> result = expenseService.getAll(null, null, null, PRINCIPAL);

		assertEquals(DESCRIPTION, result.getData().getFirst().getDescription());
		assertFalse(result.getHasNext());
		assertNull(result.getLastId());
		assertNull(result.getLastDate());

	}

	@Test
	void shouldReturnExpenseForPosition() {
		List<Expense> mockData = List.of(EXPENSE);
		KeysetScrollPosition position = ScrollPosition.of(Map.of("id", 1, "date", LocalDate.now()),
				ScrollPosition.Direction.FORWARD);

		when(expenseRepository.findFirst10ByClientOrderByDateDesc(CLIENT, position)).thenReturn(
				Window.from(mockData, new KeysetScrollPositionProvider()));

		ScrollDTO<ExpenseDTO> result = expenseService.getAll(null, 1, LocalDate.now(), PRINCIPAL);

		assertEquals(DESCRIPTION, result.getData().getFirst().getDescription());
		assertFalse(result.getHasNext());
		assertNull(result.getLastId());
		assertNull(result.getLastDate());

	}
	
	@Test
	void shouldReturnExpenseByDescription() {
		List<Expense> mockData = List.of(EXPENSE);

		when(expenseRepository.findFirst10ByDescriptionContainingIgnoreCaseAndClientOrderByDateDesc(DESCRIPTION,
				CLIENT,
				keyset())).thenReturn(
				Window.from(mockData, new KeysetScrollPositionProvider()));

		ScrollDTO<ExpenseDTO> result = expenseService.getAll(DESCRIPTION, null, null, PRINCIPAL);

		assertEquals(DESCRIPTION, result.getData().getFirst().getDescription());
		assertFalse(result.getHasNext());
		assertNull(result.getLastId());
		assertNull(result.getLastDate());
	}
	
	@Test
	void shouldNotFindExpenseByDescription() {
		when(expenseRepository.findFirst10ByDescriptionContainingIgnoreCaseAndClientOrderByDateDesc("a", CLIENT,
				keyset())).thenReturn(
				Window.from(List.of(), new KeysetScrollPositionProvider()));

		ScrollDTO<ExpenseDTO> result = expenseService.getAll("a", null, null, PRINCIPAL);
		assertTrue(result.getData().isEmpty());
		assertFalse(result.getHasNext());
		assertNull(result.getLastId());
		assertNull(result.getLastDate());
	}
	
	@Test
	void shouldReturnExpenseById() {
		when(expenseRepository.findByIdAndClient(1L, CLIENT))
		.thenReturn(Optional.of(EXPENSE));
		ResponseEntity<ExpenseDTO> income = expenseService.getOne("1");
		assertEquals(HttpStatus.OK, income.getStatusCode());
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
		ResponseEntity<ExpenseDTO> post1 = expenseService.post(FORM, PRINCIPAL);
		assertEquals(HttpStatus.CREATED, post1.getStatusCode());

		ExpenseForm form = new ExpenseForm(DESCRIPTION, VALUE, LocalDate.of(2023, 1, 1),
				Category.OTHERS.toString());
		ResponseEntity<ExpenseDTO> post2 = expenseService.post(form, PRINCIPAL);
		assertEquals(HttpStatus.CREATED, post2.getStatusCode());
	}

	@Test
	void shouldPostExpenseList() {
		when(expenseRepository.saveList(any())).thenReturn(List.of(EXPENSE));

		ResponseEntity<List<ExpenseDTO>> post = expenseService.postList(List.of(FORM), PRINCIPAL);
		Assertions.assertNotNull(post.getBody());
		List<ExpenseDTO> body = post.getBody();
		ExpenseDTO first = body.getFirst();
		assertEquals(1, body.size());
		assertEquals(first.getDescription(), FORM.getDescription());
		assertEquals(first.getDate(), FORM.getDate());
		assertEquals(first.getValue(), FORM.getValue());
		assertEquals(first.getCategory(), FORM.getCategory());
		assertEquals(HttpStatus.CREATED, post.getStatusCode());
	}
	
	//UPDATE
	@Test
	void shouldUpdateExpense() {
		when(expenseRepository.findByIdAndClient(1L, CLIENT))
		.thenReturn(Optional.of(EXPENSE));
		when(expenseRepository.getReferenceById(any()))
		.thenReturn(EXPENSE);
		ResponseEntity<ExpenseDTO> newIncome = expenseService.put("1", FORM, PRINCIPAL);
		assertEquals(HttpStatus.OK, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotFindExpenseToUpdate() {
		ResponseEntity<ExpenseDTO> newIncome = expenseService.put("100000000", FORM, PRINCIPAL);
		assertEquals(HttpStatus.NOT_FOUND, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotUpdateExpense() {
		ResponseEntity<ExpenseDTO> newIncome = expenseService.put("a", FORM, PRINCIPAL);
		assertEquals(HttpStatus.BAD_REQUEST, newIncome.getStatusCode());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteExpense() {
		when(expenseRepository.findByIdAndClient(1L, CLIENT))
		.thenReturn(Optional.of(EXPENSE));
		ResponseEntity<?> delete = expenseService.delete("1", PRINCIPAL);
		assertEquals(HttpStatus.OK, delete.getStatusCode());
	}
	
	@Test
	void shouldNotFindExpenseToDelete() {
		ResponseEntity<?> delete = expenseService.delete("100000", PRINCIPAL);
		assertEquals(HttpStatus.NOT_FOUND, delete.getStatusCode());
	}
	
	@Test
	void shouldNotDeleteExpense() {
		ResponseEntity<?> delete = expenseService.delete("a", PRINCIPAL);
		assertEquals(HttpStatus.BAD_REQUEST, delete.getStatusCode());
	}

	// Helper class to provide a KeysetScrollPosition for testing
	static class KeysetScrollPositionProvider implements IntFunction<ScrollPosition> {
		@Override
		public ScrollPosition apply(int value) {
			return ScrollPosition.keyset();
		}
	}
}
