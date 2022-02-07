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
import br.com.finances.form.ExpenseForm;
import br.com.finances.form.IncomeForm;
import br.com.finances.repository.ClientRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("dev")
@TestInstance(Lifecycle.PER_CLASS)
public class SummaryControllerTestIntegration {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ClientRepository clientRepository;
	
	private TestConstructor testConstructor = new TestConstructor();
	private List<IncomeForm> listIncomeForm = testConstructor.generateIncomeForm();
	private List<ExpenseForm> listExpenseForm = testConstructor.generateExpenseForm();
	
	@BeforeAll
	void beforeAll() throws Exception {
		MockitoAnnotations.openMocks(this);
		testConstructor.setClient();
		
		clientRepository.save(testConstructor.getListClient().get(0));
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/expense")
				.contentType(MediaType.APPLICATION_JSON)
				.content(listExpenseForm.get(0).toString()));
		
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
	
	@Test
	void shouldReturnSummaryByDate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.get("/summary/2022/01")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
		
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
