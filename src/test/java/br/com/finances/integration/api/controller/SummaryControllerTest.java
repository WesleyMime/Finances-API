package br.com.finances.integration.api.controller;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.Category;
import br.com.finances.api.expense.Expense;
import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.income.Income;
import br.com.finances.api.income.IncomeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(Lifecycle.PER_CLASS)
public class SummaryControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private IncomeRepository incomeRepository;
	@Autowired
	private ExpenseRepository expenseRepository;
	
	private static final Client CLIENT = SecurityContextFactory.setClient();
	
	@BeforeAll
	void beforeAll() {
		if((clientRepository.findByEmail(CLIENT.getUsername()).isEmpty())) {
			clientRepository.save(CLIENT);			
		}
	}
	
	@BeforeEach
	void beforeEach() {
		SecurityContextFactory.setClient();
	}	
	
	@Test
	void shouldReturnSummaryByDate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/summary/2022/01")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
	}
	
	@Test
	void shouldNotReturnSummary() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/summary/aa/aa")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

	}
	@Test
	void shouldReturnSummaryForPast12Months() throws Exception {
		populateDB();
		mockMvc.perform(MockMvcRequestBuilders
						.get("/summary/last-year")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().is2xxSuccessful(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.totalYearIncome", is(120000.0)),
						jsonPath("$.totalYearExpense", is(33000.0)),
						jsonPath("$.finalBalanceEachMonth", contains(
								10000.00, 9500.00, 9000.00, 8500.00, 8000.00, 7500.00, 7000.00,
								6500.00, 6000.00, 5500.00, 5000.00, 4500.00)),
						jsonPath("$.finalBalanceEachMonth", hasSize(12)),
						jsonPath("$.avgBalanceYear", is(7250.00)),
						jsonPath("$.percentageSavingsRate", is(72.50)),
						jsonPath("$.income", hasSize(12)),
						jsonPath("$.expenses", hasSize(12)))
				.andDo(print());
	}

	private void populateDB() {
		List<Income> incomeList = new ArrayList<>();
		List<Expense> expenseList = new ArrayList<>();
		LocalDate fromDate = LocalDate.now().minusMonths(12);

		// Populate 14 months with incomes and expenses
		for (int i = -1; i < 13; i++) {
			Income income = new Income("Income " + i,
					new BigDecimal(10000),
					fromDate.plusMonths(i));
			income.setClient(CLIENT);
			incomeList.add(income);
			Expense expense = new Expense("Expense " + i,
					new BigDecimal(i * 500),
					fromDate.plusMonths(i),
					Category.Others);
			expense.setClient(CLIENT);
			expenseList.add(expense);
		}
		incomeRepository.saveAll(incomeList);
		expenseRepository.saveAll(expenseList);
	}
}
