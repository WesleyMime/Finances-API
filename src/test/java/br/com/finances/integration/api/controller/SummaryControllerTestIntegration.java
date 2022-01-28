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
import br.com.finances.form.IncomeForm;
import br.com.finances.model.Category;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class SummaryControllerTestIntegration {

	@Autowired
	private MockMvc mockMvc;
	
	private ExpenseForm expenseForm1 = new ExpenseForm("Expense description", new BigDecimal("1500"), LocalDate.of(2022, 01, 01), Category.Home);
	private IncomeForm incomeForm1 = new IncomeForm("Income description", new BigDecimal("1500"), LocalDate.of(2022, 01, 01));
	
	@BeforeEach
	void beforeEach() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/expense")
				.contentType(MediaType.APPLICATION_JSON)
				.content(expenseForm1.toString()));
		mockMvc.perform(MockMvcRequestBuilders
				.post("/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(incomeForm1.toString()));
	}
	
	@Test
	void shouldReturnSummaryByDate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/summary/2022/01")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
		
	}
	
	@Test
	void shouldNotFindDateToReturnSummary() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/summary/1000/01")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
		
	}
	
	@Test
	void shouldNotReturnSummary() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/summary/aa/aa")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
		
	}
}
