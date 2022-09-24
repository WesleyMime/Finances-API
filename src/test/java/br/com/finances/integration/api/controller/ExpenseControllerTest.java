package br.com.finances.integration.api.controller;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.Category;
import br.com.finances.api.expense.Expense;
import br.com.finances.api.expense.ExpenseForm;
import br.com.finances.api.expense.ExpenseRepository;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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
	private static Client CLIENT = SecurityContextFactory.setClient();
	private static Long ID;
	
	@BeforeAll
	void beforeAll() {
		Optional<Client> findByEmail = clientRepository.findByEmail(CLIENT.getUsername());
		if(findByEmail.isEmpty()) {
			clientRepository.save(CLIENT);			
		} else {
			CLIENT = findByEmail.get();
		}	
		Expense expense = new Expense(DESCRIPTION, VALUE, DATE, Category.Others);
		expense.setClient(CLIENT);
		Expense saved = expenseRepository.save(expense);
		ID = saved.getId();
	}
	
	@BeforeEach
	void beforeEach() {
		SecurityContextFactory.setClient();
	}
	
	//GET
	
	@Test
	void shouldReturnAllExpenses() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/expense"))
		.andExpect(MockMvcResultMatchers
				.content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldReturnExpenseByDescription() throws Exception {			
		mockMvc.perform(MockMvcRequestBuilders
				.get("/expense?description=Description"))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotFindExpenseByDescription() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/expense?description=a"))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	@Test
	void shouldReturnExpenseById() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/expense/" + ID))
		.andExpect(MockMvcResultMatchers
				.content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotReturnExpenseById() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/expense/a"))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotFindExpenseById() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/expense/45745774"))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	@Test
	void shouldReturnExpenseByDate() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/expense/" + DATE.getYear() + "/" + DATE.getMonthValue()))
		.andExpect(MockMvcResultMatchers
				.content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotReturnExpenseByDate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/expense/aa/aa"))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotFindExpenseByDate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/expense/1000/01"))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	//POST
	
	@Test
	void shouldPostExpense() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/expense")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ExpenseForm("Different expense", VALUE, DATE, Category.Home).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isCreated());
	}
	
	@Test
	void shouldNotPostExpenseTwice() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/expense")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ExpenseForm(DESCRIPTION, VALUE, DATE, Category.Home).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotPostExpense() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/expense")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{" +
						"description"+
						":"+
						"valueDescription"
						+ "}"))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}

	//UPDATE
	
	@Test
	void shouldUpdateExpense() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/expense/" + ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ExpenseForm(DESCRIPTION, VALUE, DATE, Category.Leisure).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotFindExpenseToUpdate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/expense/1000000")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ExpenseForm(DESCRIPTION, VALUE, DATE, Category.Education).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	@Test
	void shouldNotUpdateExpense() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/expense/a")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ExpenseForm(null, null, null, null).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteExpense() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/expense/" + ID))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
		
	}
	
	@Test
	void shouldNotFindExpenseToDelete() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/expense/100000000000"))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
		
	}
	
	@Test
	void shouldNotDeleteExpense() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/expense/a"))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
		
	}
	
}
