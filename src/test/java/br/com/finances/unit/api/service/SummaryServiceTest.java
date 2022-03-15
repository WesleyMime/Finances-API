package br.com.finances.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.security.Principal;
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
import br.com.finances.api.expense.ExpenseCategoryDTO;
import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.income.IncomeRepository;
import br.com.finances.api.summary.SummaryDTO;
import br.com.finances.api.summary.SummaryService;

public class SummaryServiceTest {

	@Mock
	private IncomeRepository incomeRepository;
	@Mock
	private ExpenseRepository expenseRepository;
	@Mock
	private ClientRepository clientRepository;
	@Mock
	private Principal principal;
	
	private SummaryService summaryService;
	
	private static final Client CLIENT = SecurityContextFactory.setClient();
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		SecurityContextFactory.setClient();
		
		summaryService = new SummaryService(incomeRepository, expenseRepository, clientRepository);
		
		Optional<BigDecimal> optionalTotal = Optional.of(new BigDecimal(7500));
		
		when(incomeRepository.totalIncomeMonth(2022, 01, CLIENT))
		.thenReturn(optionalTotal);
		
		when(expenseRepository.totalExpenseMonth(2022, 01, CLIENT))
		.thenReturn(optionalTotal);
		
		when(expenseRepository.totalExpenseByCategory(2022, 01, CLIENT))
		.thenReturn(List.of(new ExpenseCategoryDTO(Category.Food, new BigDecimal(1500))));
		
		when(clientRepository.findByEmail(any()))
		.thenReturn(Optional.of(CLIENT));
		
	}
	
	@Test
	void shouldReturnTotalIncomeInSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01", principal);
		BigDecimal totalIncome = summary.getBody().getTotalIncome();
		assertEquals(new BigDecimal(7500), totalIncome);
	}
	
	@Test
	void shouldReturnTotalExpenseInSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01", principal);
		BigDecimal totalExpense = summary.getBody().getTotalExpense();
		assertEquals(new BigDecimal(7500), totalExpense);
	}
	
	@Test
	void shouldReturnFinalBalanceInSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01", principal);
		BigDecimal finalBalance = summary.getBody().getFinalBalance();
		assertEquals(new BigDecimal(0), finalBalance);
	}
	
	@Test
	void shouldReturnExpenseByCategoryInSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01", principal);
		List<ExpenseCategoryDTO> expenseCategory = summary.getBody().getTotalExpenseByCategory();
		Category category = expenseCategory.get(0).getCategory();
		BigDecimal totalValue = expenseCategory.get(0).getTotalValue();
		assertEquals(Category.Food, category);
		assertEquals(new BigDecimal(1500), totalValue);
	}
	
	@Test
	void shouldNotReturnSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("aa", "aa", principal);
		assertEquals(HttpStatus.BAD_REQUEST, summary.getStatusCode());
	}
}
