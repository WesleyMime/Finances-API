package br.com.finances.integration.api.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.income.Income;
import br.com.finances.api.income.IncomeForm;
import br.com.finances.api.income.IncomeRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("prod")
@TestInstance(Lifecycle.PER_CLASS)
class IncomeControllerTestIntegration {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private IncomeRepository incomeRepository;
	
	private static final String DESCRIPTION = "Description";
	private static final BigDecimal VALUE = new BigDecimal("1500");
	private static final LocalDate DATE = LocalDate.of(2022, 01, 01);
	private static Client CLIENT = SecurityContextFactory.setClient();
	private static Long ID;
		
		
	@BeforeAll
	void beforeAll() throws Exception {
		Optional<Client> findByEmail = clientRepository.findByEmail(CLIENT.getUsername());
		if(findByEmail.isEmpty()) {
			clientRepository.save(CLIENT);			
		} else {
			CLIENT = findByEmail.get();
		}
		Income income = new Income(DESCRIPTION, VALUE, DATE);
		income.setClient(CLIENT);
		Income saved = incomeRepository.save(income);
		ID = saved.getId();
	}

	@BeforeEach
	void beforeEach() {
		SecurityContextFactory.setClient();
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
	void shouldReturnIncomeByDescription() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income?description=Description"))
		.andExpect(MockMvcResultMatchers
				.content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotFindIncomeByDescription() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income?description=a"))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}	
	
	@Test
	void shouldReturnIncomeById() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income/" + ID))
		.andExpect(MockMvcResultMatchers
				.content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotFindIncomeById() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income/87678767876"))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	@Test
	void shouldNotReturnIncomeById() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income/a"))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}	
	
	@Test
	void shouldFindIncomeByDate() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income/2022/01"))
		.andExpect(MockMvcResultMatchers
				.content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotFindIncomeByDate() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income/1000/01"))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
		
	@Test
	void shouldNotReturnIncomeByDate() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/income/a/a"))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	//POST
	
	@Test
	void shouldPostIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new IncomeForm("New Description", VALUE, DATE).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isCreated());
	}
	
	@Test
	void shouldNotPostSameIncomeTwice() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new IncomeForm(DESCRIPTION, VALUE, DATE).toString()))
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
				.put("/income/" + ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new IncomeForm(DESCRIPTION, VALUE, DATE.plusDays(5)).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotFindIncomeToUpdate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/income/1000000")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new IncomeForm(DESCRIPTION, VALUE, DATE).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	@Test
	void shouldNotUpdateIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/income/a")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new IncomeForm(DESCRIPTION, VALUE, DATE).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.delete("/income/" + ID))
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
