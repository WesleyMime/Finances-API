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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

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

        when(incomeRepository.totalIncomeMonth(2022, 01, CLIENT)).thenReturn(optionalTotal);

        when(expenseRepository.totalExpenseMonth(2022, 01, CLIENT)).thenReturn(optionalTotal);

        when(expenseRepository.totalExpenseByCategory(2022, 01, CLIENT)).thenReturn(
                List.of(new ExpenseCategoryDTO(Category.Food, new BigDecimal(1500))));

        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(CLIENT));

    }

    @Test
    void shouldReturnTotalIncomeInSummary() {
        ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01",
                principal);
        BigDecimal totalIncome = summary.getBody().totalIncome();
        assertEquals(new BigDecimal(7500), totalIncome);
    }

    @Test
    void shouldReturnTotalExpenseInSummary() {
        ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01",
                principal);
        BigDecimal totalExpense = summary.getBody().totalExpense();
        assertEquals(new BigDecimal(7500), totalExpense);
    }

    @Test
    void shouldReturnFinalBalanceInSummary() {
        ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01",
                principal);
        BigDecimal finalBalance = summary.getBody().finalBalance();
        assertEquals(new BigDecimal(0), finalBalance);
    }

    @Test
    void shouldReturnExpenseByCategoryInSummary() {
        ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("2022", "01",
                principal);
        List<ExpenseCategoryDTO> expenseCategory = summary.getBody().totalExpenseByCategory();
        Category category = expenseCategory.get(0).category();
        BigDecimal totalValue = expenseCategory.get(0).totalValue();
        assertEquals(Category.Food, category);
        assertEquals(new BigDecimal(1500), totalValue);
    }

    @Test
    void shouldNotReturnSummary() {
        ResponseEntity<SummaryDTO> summary = summaryService.getSummaryByDate("aa", "aa", principal);
        assertEquals(HttpStatus.BAD_REQUEST, summary.getStatusCode());
    }

    @Test
    void returnsCorrectBalancesFor12Months() {
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(new BigDecimal(1000)));
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(new BigDecimal(500)));

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(principal);

        assertEquals(12, summary.finalBalanceEachMonth().size());
        assertEquals(new BigDecimal(500), summary.finalBalanceEachMonth().get(0));
        assertEquals(new BigDecimal(500), summary.finalBalanceEachMonth().get(11));
    }

    @Test
    void handlesZeroIncomeAndExpenses() {
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(BigDecimal.ZERO));
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(BigDecimal.ZERO));

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(principal);

        assertEquals(12, summary.finalBalanceEachMonth().size());
        assertTrue(summary.finalBalanceEachMonth().stream().allMatch(
                balance -> balance.equals(BigDecimal.ZERO)));
    }

    @Test
    void handlesMissingIncomeOrExpenseData() {
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.empty());
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.empty());

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(principal);

        assertEquals(12, summary.finalBalanceEachMonth().size());
        assertTrue(summary.finalBalanceEachMonth().stream().allMatch(
                balance -> balance.equals(BigDecimal.ZERO)));
    }

    @Test
    void calculatesCorrectCumulativeBalances() {
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(new BigDecimal(2000)));
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any())).thenReturn(
                Optional.of(new BigDecimal(500)));

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(principal);

        BigDecimal expectedAverageBalance = new BigDecimal(1500);
        BigDecimal actualAverageBalance = summary.avgBalanceYear();

        assertEquals(expectedAverageBalance, actualAverageBalance);
    }

    @Test
    void calculatesCorrectBalancesForYearWithVaryingIncomeAndExpenses() {
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new BigDecimal(5000)))
                .thenReturn(Optional.of(new BigDecimal(3000)));
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new BigDecimal(2000)))
                .thenReturn(Optional.of(new BigDecimal(1000)));

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(principal);
        assertEquals(new BigDecimal(2083), summary.avgBalanceYear());
        assertEquals(new BigDecimal(20), summary.percentageSavingsRate());
    }

    @Test
    void handlesLeapYearCorrectly() {
        LocalDate leapYearDate = LocalDate.of(2020, 2, 29);
        when(incomeRepository.totalIncomeMonth(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new BigDecimal(1000)));
        when(expenseRepository.totalExpenseMonth(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new BigDecimal(500)));

        SummaryLastYearDTO summary = summaryService.getSummaryOfLastYear(principal);

        assertEquals(12, summary.finalBalanceEachMonth().size());
        assertTrue(summary.finalBalanceEachMonth().stream()
                .allMatch(balance -> balance.equals(new BigDecimal(500))));
    }
}
