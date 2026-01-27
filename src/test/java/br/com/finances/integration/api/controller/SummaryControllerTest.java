package br.com.finances.integration.api.controller;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.Category;
import br.com.finances.api.expense.Expense;
import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.income.Income;
import br.com.finances.api.income.IncomeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import redis.embedded.RedisServer;

import java.io.IOException;
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
class SummaryControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private IncomeRepository incomeRepository;
	@Autowired
	private ExpenseRepository expenseRepository;

	private RedisServer redisServer;

	
	private static final Client CLIENT = SecurityContextFactory.setClient();
	
	@BeforeAll
	void beforeAll() throws IOException {
		clientRepository.deleteAll();
		expenseRepository.deleteAll();
		incomeRepository.deleteAll();
		this.redisServer = new RedisServer(6379);
		redisServer.start();
		if((clientRepository.findByEmail(CLIENT.getUsername()).isEmpty())) {
			clientRepository.save(CLIENT);			
		}
		populateDB();
	}
	
	@BeforeEach
	void beforeEach() {
		SecurityContextFactory.setClient();
	}

	@AfterAll
	void afterAll() throws IOException {
		redisServer.stop();
	}
	
	@Test
	void shouldReturnSummaryByMonth() throws Exception {
		LocalDate date = LocalDate.now().minusMonths(1);
		mockMvc.perform(MockMvcRequestBuilders
						.get("/summary/" + date.getYear() + "/" + date.getMonthValue())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().is2xxSuccessful(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.totalIncome", is(10000.0)),
						jsonPath("$.totalExpense", is(5500.0)),
						jsonPath("$.finalBalance", is(4500.00)),
						jsonPath("$.incomeList", hasSize(1)),
						jsonPath("$.totalExpenseByCategory", hasSize(1)))
				.andDo(print());

	}

	@Test
	void shouldReturnEmptySummaryByMonth() throws Exception {
		LocalDate date = LocalDate.now().plusMonths(1);
		mockMvc.perform(MockMvcRequestBuilders
						.get("/summary/" + date.getYear() + "/" + date.getMonthValue())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().is2xxSuccessful(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.totalIncome", is(0)),
						jsonPath("$.totalExpense", is(0)),
						jsonPath("$.finalBalance", is(0)),
						jsonPath("$.incomeList", hasSize(0)),
						jsonPath("$.totalExpenseByCategory", hasSize(0)))
				.andDo(print());

	}

	@Test
	void shouldReturnSummaryByDate() throws Exception {
		LocalDate from = LocalDate.now().minusMonths(13);
		LocalDate to = from.plusMonths(6);
		mockMvc.perform(MockMvcRequestBuilders
						.get("/summary")
						.param("yearFrom", String.valueOf(from.getYear()))
						.param("monthFrom", String.valueOf(from.getMonthValue()))
						.param("yearTo", String.valueOf(to.getYear()))
						.param("monthTo", String.valueOf(to.getMonthValue()))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().is2xxSuccessful(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.totalIncomePeriod", is(1060000.0)),
						jsonPath("$.totalExpensePeriod", is(507500.0)),
						jsonPath("$.totalBalancePeriod", is(552500.0)))
				.andDo(print());

	}

	@Test
	void shouldReturnEmptySummaryByDate() throws Exception {
		LocalDate from = LocalDate.now().minusYears(3);
		LocalDate to = LocalDate.now().minusYears(2);
		mockMvc.perform(MockMvcRequestBuilders
						.get("/summary"
								+ "?yearFrom=" + from.getYear()
								+ "&monthFrom=" + from.getMonthValue()
								+ "&yearTo=" + to.getYear()
								+ "&monthTo=" + to.getMonthValue())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().is2xxSuccessful(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.totalIncomePeriod", is(0)),
						jsonPath("$.totalExpensePeriod", is(0)),
						jsonPath("$.totalBalancePeriod", is(0)))
				.andDo(print());

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

	@Test
	void shouldReturnAccountSummary() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
						.get("/summary/account")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						status().is2xxSuccessful(),
						content().contentType(MediaType.APPLICATION_JSON),
						jsonPath("$.totalIncome", is(1120000.0)),
						jsonPath("$.totalExpense", is(533000.0)),
						jsonPath("$.totalBalance", is(587000.0)))
				.andDo(print());

	}

	private void populateDB() {
		List<Income> incomeList = new ArrayList<>();
		List<Expense> expenseList = new ArrayList<>();
		LocalDate fromDate = LocalDate.now().minusMonths(12);


		Income income = new Income("Income -1", new BigDecimal(1000000), fromDate.minusMonths(1));
		income.setClient(CLIENT);
		incomeList.add(income);
		Expense expense = new Expense("Expense -1", new BigDecimal(500000), fromDate.minusMonths(1), Category.OTHERS);
		expense.setClient(CLIENT);
		expenseList.add(expense);

		// Populate 12 months with incomes and expenses
		for (int i = 0; i < 12; i++) {
			income = new Income("Income " + i,
					new BigDecimal(10000),
					fromDate.plusMonths(i));
			income.setClient(CLIENT);
			incomeList.add(income);
			expense = new Expense("Expense " + i,
					new BigDecimal(i * 500),
					fromDate.plusMonths(i),
					Category.OTHERS);
			expense.setClient(CLIENT);
			expenseList.add(expense);
		}

		income = new Income("Income 13", new BigDecimal(1000000), fromDate.plusMonths(12).plusDays(1));
		income.setClient(CLIENT);
		incomeList.add(income);
		expense = new Expense("Expense 13", new BigDecimal(500000), fromDate.plusMonths(12).plusDays(1),
				Category.OTHERS);
		expense.setClient(CLIENT);
		expenseList.add(expense);

		incomeRepository.saveAll(incomeList);
		expenseRepository.saveAll(expenseList);
	}
}
