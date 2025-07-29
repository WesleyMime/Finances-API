package br.com.finances.integration.api.controller;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.Category;
import br.com.finances.api.expense.Expense;
import br.com.finances.api.expense.ExpenseForm;
import br.com.finances.api.expense.ExpenseRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(Lifecycle.PER_CLASS)
class ExpenseControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private ExpenseRepository expenseRepository;
	
	private static final String DESCRIPTION = "Description";
	private static final BigDecimal VALUE = new BigDecimal("1500");
	private static final LocalDate DATE = LocalDate.of(2022, 1, 1);
	private static Client client = SecurityContextFactory.setClient();
	private static Long id;
	private static final String ENDPOINT = "/expense";

	private RedisServer redisServer;

	@BeforeAll
	void beforeAll() throws IOException {
		this.redisServer = new RedisServer(6379);
		redisServer.start();
		Optional<Client> findByEmail = clientRepository.findByEmail(client.getUsername());
		if(findByEmail.isEmpty()) {
			clientRepository.save(client);
		} else {
			client = findByEmail.get();
		}
		Expense expense = new Expense(DESCRIPTION, VALUE, DATE, Category.OTHERS);
		expense.setClient(client);
		Expense saved = expenseRepository.save(expense);
		id = saved.getId();
	}

	@BeforeEach
	void beforeEach() {
		SecurityContextFactory.setClient();
	}

	@AfterAll
	void afterAll() throws IOException {
		redisServer.stop();
	}
	
	//GET
	
	@Test
	void shouldReturnAllExpenses() throws Exception {
		mockMvc.perform(get(ENDPOINT))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldReturnExpenseByDescription() throws Exception {
		mockMvc.perform(get(ENDPOINT + "?description=Description"))
				.andExpect(status().isOk());
		mockMvc.perform(get(ENDPOINT + "?description=description"))
				.andExpect(status().isOk());
		mockMvc.perform(get(ENDPOINT + "?description=cript"))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldNotFindExpenseByDescription() throws Exception {
		mockMvc.perform(get(ENDPOINT + "?description=a"))
				.andExpect(status().isNotFound());
	}
	
	@Test
	void shouldReturnExpenseById() throws Exception {
		mockMvc.perform(get(ENDPOINT + "/" + id))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldNotReturnExpenseById() throws Exception {
		mockMvc.perform(get(ENDPOINT + "/a"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldNotFindExpenseById() throws Exception {
		mockMvc.perform(get(ENDPOINT + "/45745774"))
				.andExpect(status().isNotFound());
	}
	
	@Test
	void shouldReturnExpenseByDate() throws Exception {

		mockMvc.perform(get(ENDPOINT + "/" + DATE.getYear() + "/" + DATE.getMonthValue()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldNotReturnExpenseByDate() throws Exception {
		mockMvc.perform(get(ENDPOINT + "/aa/aa"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldNotFindExpenseByDate() throws Exception {
		mockMvc.perform(get(ENDPOINT + "/1000/01"))
				.andExpect(status().isNotFound());
	}
	
	//POST
	
	@Test
	void shouldPostExpense() throws Exception {
		mockMvc.perform(post(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
						.content(new ExpenseForm("Different expense", VALUE, DATE, Category.HOME.toString()).toString()))
				.andExpect(status().isCreated());
		LocalDate date = LocalDate.of(2023, 1, 1);
		mockMvc.perform(post(ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ExpenseForm("Different expense", VALUE, date,
								Category.HOME.toString()).toString()))
				.andExpect(status().isCreated());
	}
	
	@Test
	void shouldNotPostExpenseTwice() throws Exception {
		mockMvc.perform(post(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
						.content(new ExpenseForm(DESCRIPTION, VALUE, DATE, Category.HOME.toString()).toString()))
				.andExpect(status().isConflict());
	}
	
	@Test
	void shouldNotPostExpense() throws Exception {
		mockMvc.perform(post(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{" +
						"description"+
						":"+
						"valueDescription"
						+ "}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldPostExpenseList() throws Exception {
		mockMvc.perform(post(ENDPOINT + "/list")
						.contentType(MediaType.APPLICATION_JSON)
						.content(List.of(
								new ExpenseForm("Expense1", VALUE, DATE, Category.HOME.toString()).toString(),
								new ExpenseForm("Expense2", VALUE, DATE, Category.LEISURE.toString()).toString()
						).toString()))
				.andExpectAll(
						jsonPath("[*].id", notNullValue()),
						jsonPath("[*].description", containsInAnyOrder("Expense1", "Expense2")),
						jsonPath("[*].value", contains(1500, 1500)),
						jsonPath("[*].date", contains(DATE.toString(), DATE.toString())),
						jsonPath("[*].category", containsInAnyOrder(Category.HOME.toString(),
								Category.LEISURE.toString())),
						status().isCreated())
				.andDo(print());
	}

	@Test
	void shouldNotPostExpenseListTwice() throws Exception {
		mockMvc.perform(post(ENDPOINT + "/list")
						.contentType(MediaType.APPLICATION_JSON)
						.content(List.of(
								new ExpenseForm("Expense3", VALUE, DATE, Category.HOME.toString()).toString(),
								new ExpenseForm("Expense3", VALUE, DATE, Category.HOME.toString()).toString()
						).toString()))
				.andExpectAll(
						jsonPath("$", Matchers.hasSize(1)),
						jsonPath("[0].id", notNullValue()),
						jsonPath("[0].description", is("Expense3")),
						jsonPath("[0].value", is(1500)),
						jsonPath("[0].date", is(DATE.toString())),
						jsonPath("[0].category", is(Category.HOME.toString())),
						status().isCreated())
				.andDo(print());
	}

	@Test
	void shouldNotPostExpenseList() throws Exception {
		mockMvc.perform(post(ENDPOINT + "/list")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ExpenseForm("Different expense", VALUE, DATE,
								Category.HOME.toString()).toString()))
				.andExpect(
						status().isBadRequest())
				.andDo(print());
	}

	//UPDATE
	
	@Test
	void shouldUpdateExpense() throws Exception {
		mockMvc.perform(put(ENDPOINT + "/" + id)
				.contentType(MediaType.APPLICATION_JSON)
						.content(new ExpenseForm(DESCRIPTION, VALUE, DATE, Category.LEISURE.toString()).toString()))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldNotFindExpenseToUpdate() throws Exception {
		mockMvc.perform(put(ENDPOINT + "/1000000")
				.contentType(MediaType.APPLICATION_JSON)
						.content(new ExpenseForm(DESCRIPTION, VALUE, DATE, Category.EDUCATION.toString()).toString()))
				.andExpect(status().isNotFound());
	}
	
	@Test
	void shouldNotUpdateExpense() throws Exception {
		mockMvc.perform(put(ENDPOINT + "/a")
				.contentType(MediaType.APPLICATION_JSON)
						.content(new ExpenseForm(null, null, null, null).toString()))
				.andExpect(status().isBadRequest());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteExpense() throws Exception {
		mockMvc.perform(delete(ENDPOINT + "/" + id))
				.andExpect(status().isOk());
		
	}
	
	@Test
	void shouldNotFindExpenseToDelete() throws Exception {
		mockMvc.perform(delete(ENDPOINT + "/100000000000"))
				.andExpect(status().isNotFound());
		
	}
	
	@Test
	void shouldNotDeleteExpense() throws Exception {
		mockMvc.perform(delete(ENDPOINT + "/a"))
				.andExpect(status().isBadRequest());
		
	}
	
}
