package br.com.finances.integration.api.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.finances.form.ExpenseForm;
import br.com.finances.model.Category;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ExpenseControllerTestIntegration {
	
	@Autowired
	private MockMvc mockMvc;
	
	private ExpenseForm expenseForm1 = new ExpenseForm("Expense description", new BigDecimal("1500"), LocalDate.of(2022, 01, 01), Category.Home);
	private ExpenseForm expenseForm2 = new ExpenseForm("Description expense", new BigDecimal("3000"), LocalDate.of(2022, 02, 01), Category.Others);
	private ExpenseForm expenseForm3 = new ExpenseForm("Expense description", new BigDecimal("1000"), LocalDate.of(2022, 01, 25), Category.Unforeseen);
	private ExpenseForm expenseForm4 = new ExpenseForm("Description expense", new BigDecimal("2000"), LocalDate.of(2022, 03, 25), null);
	
	@BeforeEach
	void beforeEach() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/expense")
				.contentType(MediaType.APPLICATION_JSON)
				.content(expenseForm1.toString()));
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
				.get("/expense?description=Expense description"))
		.andExpect(MockMvcResultMatchers
				.content().contentType(MediaType.APPLICATION_JSON))
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
				.get("/expense/1"))
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
				.get("/expense/2022/01"))
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
				.content(expenseForm2.toString()))
		.andExpect(MockMvcResultMatchers
				.status().isCreated());
	}
	
	@Test
	void shouldNotPostExpenseTwice() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/expense")
				.contentType(MediaType.APPLICATION_JSON)
				.content(expenseForm3.toString()))
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
				.put("/expense/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(expenseForm4.toString()))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotFindExpenseToUpdate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/expense/1000000")
				.contentType(MediaType.APPLICATION_JSON)
				.content(expenseForm1.toString()))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	@Test
	void shouldNotUpdateExpense() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/expense/a")
				.contentType(MediaType.APPLICATION_JSON)
				.content(expenseForm1.toString()))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteExpense() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/expense/1"))
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
