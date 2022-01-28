package br.com.finances.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.finances.api.service.SummaryService;
import br.com.finances.dto.ExpenseCategoryDTO;
import br.com.finances.dto.SummaryDTO;
import br.com.finances.model.Category;
import br.com.finances.repository.ExpenseRepository;
import br.com.finances.repository.IncomeRepository;

public class SummaryServiceTest {

	@Mock
	private IncomeRepository incomeRepository;
	@Mock
	private ExpenseRepository expenseRepository;
	
	private SummaryService summaryService;
	
	private List<ExpenseCategoryDTO> listExpenseCategoryDto = new ArrayList<>();
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		
		summaryService = new SummaryService(incomeRepository, expenseRepository);
		
		Mockito.when(incomeRepository.totalIncomeMonth(2022, 01))
		.thenReturn(new BigDecimal(7500));
		
		Mockito.when(expenseRepository.totalExpenseMonth(2022, 01))
		.thenReturn(new BigDecimal(7500));
		
		listExpenseCategoryDto.add(new ExpenseCategoryDTO(Category.Food, new BigDecimal(1500)));
		Mockito.when(expenseRepository.totalExpenseByCategory(2022, 01))
		.thenReturn(listExpenseCategoryDto);
	}
	
	@Test
	void shouldReturnTotalIncomeInSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01");
		BigDecimal totalIncome = summary.getBody().getTotalIncome();
		assertEquals(new BigDecimal(7500), totalIncome);
	}
	
	@Test
	void shouldReturnTotalExpenseInSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01");
		BigDecimal totalExpense = summary.getBody().getTotalExpense();
		assertEquals(new BigDecimal(7500), totalExpense);
	}
	
	@Test
	void shouldReturnFinalBalanceInSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01");
		BigDecimal finalBalance = summary.getBody().getFinalBalance();
		assertEquals(new BigDecimal(0), finalBalance);
	}
	
	@Test
	void shouldReturnExpenseByCategoryInSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01");
		List<ExpenseCategoryDTO> expenseCategory = summary.getBody().getTotalExpenseByCategory();
		Category category = expenseCategory.get(0).getCategory();
		BigDecimal totalValue = expenseCategory.get(0).getTotalValue();
		assertEquals(Category.Food, category);
		assertEquals(new BigDecimal(1500), totalValue);
	}
	
	@Test
	void shouldNotFindDateToReturnSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("1000", "01");
		assertEquals(HttpStatus.NOT_FOUND, summary.getStatusCode());
	}
	
	@Test
	void shouldNotReturnSummary() {
		ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("aa", "aa");
		assertEquals(HttpStatus.BAD_REQUEST, summary.getStatusCode());
	}
}