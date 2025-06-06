package br.com.finances.unit.api.service;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.Category;
import br.com.finances.api.expense.ExpenseCategoryDTO;
import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.income.IncomeRepository;
import br.com.finances.api.summary.SummaryDTO;
import br.com.finances.api.summary.SummaryLastYearDTO;
import br.com.finances.api.summary.SummaryService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class SummaryServiceTest {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final ClientRepository clientRepository;
    private final SummaryService summaryService;
    private static final Client CLIENT = SecurityContextFactory.setClient();

    public SummaryServiceTest() {
        this.incomeRepository = Mockito.mock(IncomeRepository.class);
        this.expenseRepository = Mockito.mock(ExpenseRepository.class);
        this.clientRepository = Mockito.mock(ClientRepository.class);
        this.summaryService = new SummaryService(incomeRepository, expenseRepository,
                clientRepository);
    }

    @BeforeEach
    void beforeEach() {
        Optional<BigDecimal> optionalTotal = Optional.of(new BigDecimal(7500));
        when(incomeRepository.totalIncomeMonth(2022, 1, CLIENT))
                .thenReturn(optionalTotal);
        when(expenseRepository.totalExpenseMonth(2022, 1, CLIENT))
                .thenReturn(optionalTotal);
        when(expenseRepository.totalExpenseByCategory(2022, 1, CLIENT))
                .thenReturn(
                List.of(new ExpenseCategoryDTO(Category.Food, new BigDecimal(1500))));
        when(clientRepository.findByEmail(any()))
                .thenReturn(Optional.of(CLIENT));

    }

    @Test
    void shouldReturnTotalIncomeInSummary() {
        ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01");
		Assertions.assertNotNull(summary.getBody());
        BigDecimal totalIncome = summary.getBody().totalIncome();
        assertEquals(new BigDecimal(7500), totalIncome);
    }

    @Test
    void shouldReturnTotalExpenseInSummary() {
        ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01");
		Assertions.assertNotNull(summary.getBody());
        BigDecimal totalExpense = summary.getBody().totalExpense();
        assertEquals(new BigDecimal(7500), totalExpense);
    }

    @Test
    void shouldReturnFinalBalanceInSummary() {
        ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01");
		Assertions.assertNotNull(summary.getBody());
        BigDecimal finalBalance = summary.getBody().finalBalance();
        assertEquals(new BigDecimal(0), finalBalance);
    }

    @Test
    void shouldReturnExpenseByCategoryInSummary() {
        ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01");
		Assertions.assertNotNull(summary.getBody());
        List<ExpenseCategoryDTO> expenseCategory = summary.getBody().totalExpenseByCategory();
		Category category = expenseCategory.getFirst().category();
		BigDecimal totalValue = expenseCategory.getFirst().totalValue();
        assertEquals(Category.Food, category);
        assertEquals(new BigDecimal(1500), totalValue);
    }

    @Test
    void shouldNotReturnSummary() {
        ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("aa", "aa");
        assertEquals(HttpStatus.BAD_REQUEST, summary.getStatusCode());
    }

    @Test
    void returnsCorrectBalancesFor12Months() {
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(new BigDecimal(1000)));
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(new BigDecimal(500)));

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(LocalDate.now());

		assertEquals(new BigDecimal(12000), summary.totalYearIncome());
		assertEquals(new BigDecimal(6000), summary.totalYearExpense());
		assertEquals(new BigDecimal(500), summary.avgBalanceYear());
		assertEquals(new BigDecimal("50.00"), summary.percentageSavingsRate());
        assertEquals(12, summary.finalBalanceEachMonth().size());
		assertEquals(new BigDecimal(500), summary.finalBalanceEachMonth().getFirst());
        assertEquals(new BigDecimal(500), summary.finalBalanceEachMonth().get(11));
    }

    @Test
    void handlesZeroIncomeAndExpenses() {
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(BigDecimal.ZERO));
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(BigDecimal.ZERO));

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(LocalDate.now());

		assertEquals(new BigDecimal(0), summary.totalYearIncome());
		assertEquals(new BigDecimal(0), summary.totalYearExpense());
		assertEquals(new BigDecimal(0), summary.avgBalanceYear());
		assertEquals(new BigDecimal("0"), summary.percentageSavingsRate());
		assertEquals(12, summary.finalBalanceEachMonth().size());
        assertEquals(12, summary.finalBalanceEachMonth().size());
        assertTrue(summary.finalBalanceEachMonth().stream().allMatch(
                balance -> balance.equals(BigDecimal.ZERO)));
    }

    @Test
    void handlesMissingIncomeOrExpenseData() {
		LocalDate date = LocalDate.of(1970, 1, 1);
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.empty());
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.empty());

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(date);

		assertEquals(new BigDecimal(0), summary.totalYearIncome());
		assertEquals(new BigDecimal(0), summary.totalYearIncome());
		assertEquals(new BigDecimal(0), summary.avgBalanceYear());
		assertEquals(new BigDecimal("0"), summary.percentageSavingsRate());
        assertEquals(12, summary.finalBalanceEachMonth().size());
        assertTrue(summary.finalBalanceEachMonth().stream().allMatch(
                balance -> balance.equals(BigDecimal.ZERO)));
    }

    @Test
    void calculatesCorrectCumulativeBalances() {
		LocalDate date = LocalDate.of(2100, 12, 31);
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(new BigDecimal(2000)));
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(new BigDecimal(500)));

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(date);

		assertEquals(new BigDecimal(24000), summary.totalYearIncome());
		assertEquals(new BigDecimal(6000), summary.totalYearExpense());
		assertEquals(new BigDecimal(1500), summary.avgBalanceYear());
		assertEquals(new BigDecimal("75.00"), summary.percentageSavingsRate());
    }

    @Test
    void calculatesCorrectBalancesForYearWithVaryingIncomeAndExpenses() {
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new BigDecimal(5000)))
                .thenReturn(Optional.of(new BigDecimal(3000)));
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new BigDecimal(2000)))
                .thenReturn(Optional.of(new BigDecimal(1000)));

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(LocalDate.now());

		assertEquals(new BigDecimal(38000), summary.totalYearIncome());
		assertEquals(new BigDecimal(13000), summary.totalYearExpense());
        assertEquals(new BigDecimal(2083), summary.avgBalanceYear());
        assertEquals(new BigDecimal("65.79"), summary.percentageSavingsRate());
    }

    @Test
    void handlesLeapYearCorrectly() {
        LocalDate leapYearDate = LocalDate.of(2020, 2, 29);
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new BigDecimal(1000)));
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new BigDecimal(500)));

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(leapYearDate);

		assertEquals(new BigDecimal(12000), summary.totalYearIncome());
		assertEquals(new BigDecimal(6000), summary.totalYearExpense());
        assertEquals(12, summary.finalBalanceEachMonth().size());
        assertTrue(summary.finalBalanceEachMonth().stream()
                .allMatch(balance -> balance.equals(new BigDecimal(500))));
    }
}
