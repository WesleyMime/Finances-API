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

import br.com.finances.form.IncomeForm;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class IncomeControllerTestIntegration {
	
	@Autowired
	private MockMvc mockMvc;
	
	private IncomeForm incomeForm1 = new IncomeForm("Income description", new BigDecimal("1500"), LocalDate.of(2022, 01, 01));
	private IncomeForm incomeForm2 = new IncomeForm("Description income", new BigDecimal("3000"), LocalDate.of(2022, 02, 01));
	private IncomeForm incomeForm3 = new IncomeForm("Income description", new BigDecimal("1000"), LocalDate.of(2022, 01, 25));
	private IncomeForm incomeForm4 = new IncomeForm("Description income", new BigDecimal("2000"), LocalDate.of(2022, 03, 25));
	
	@BeforeEach
	void beforeEach() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(incomeForm1.toString()));
	}

	//GET
	
	@Test
	void shouldReturnAllIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income"))
		.andExpect(MockMvcResultMatchers
				.content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldReturnIncomeById() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income/1"))
		.andExpect(MockMvcResultMatchers
				.content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotReturnIncomeById() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income/a"))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotFindIncomeById() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income/45745774"))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	//POST
	
	@Test
	void shouldPostIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(incomeForm2.toString()))
		.andExpect(MockMvcResultMatchers
				.status().isCreated());
	}
	
	@Test
	void shouldNotPostSameIncomeTwice() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(incomeForm3.toString()))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotPostIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/income")
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
	void shouldUpdateIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/income/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(incomeForm4.toString()))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotFindIncomeToUpdate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/income/1000000")
				.contentType(MediaType.APPLICATION_JSON)
				.content(incomeForm1.toString()))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	@Test
	void shouldNotUpdateIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/income/a")
				.contentType(MediaType.APPLICATION_JSON)
				.content(incomeForm1.toString()))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/income/1"))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
		
	}
	
	@Test
	void shouldNotFindIncomeToDelete() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/income/100000000000"))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
		
	}
	
	@Test
	void shouldNotDeleteIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/income/a"))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
		
	}
	
}
