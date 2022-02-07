package br.com.finances.integration.api.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockitoAnnotations;
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

import br.com.finances.TestConstructor;
import br.com.finances.form.IncomeForm;
import br.com.finances.repository.ClientRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("dev")
@TestInstance(Lifecycle.PER_CLASS)
class IncomeControllerTestIntegration {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ClientRepository clientRepository;
	
	private TestConstructor testConstructor = new TestConstructor();
	private List<IncomeForm> listIncomeForm = testConstructor.generateIncomeForm();
	
		
	@BeforeAll
	void beforeAll() throws Exception {
		MockitoAnnotations.openMocks(this);
		testConstructor.setClient();
		
		clientRepository.save(testConstructor.getListClient().get(0));
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(listIncomeForm.get(0).toString()));
	}

	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		testConstructor.setClient();
	
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
				.get("/income?description=Income description"))
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
				.get("/income/1"))
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
				.content(listIncomeForm.get(1).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isCreated());
	}
	
	@Test
	void shouldNotPostSameIncomeTwice() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/income")
				.contentType(MediaType.APPLICATION_JSON)
				.content(listIncomeForm.get(2).toString()))
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
				.content(listIncomeForm.get(3).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotFindIncomeToUpdate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/income/1000000")
				.contentType(MediaType.APPLICATION_JSON)
				.content(listIncomeForm.get(0).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	@Test
	void shouldNotUpdateIncome() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/income/a")
				.contentType(MediaType.APPLICATION_JSON)
				.content(listIncomeForm.get(0).toString()))
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
