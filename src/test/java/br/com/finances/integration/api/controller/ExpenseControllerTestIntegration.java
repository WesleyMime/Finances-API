package br.com.finances.integration.api.controller;

import java.time.LocalDate;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.finances.TestConstructor;
import br.com.finances.form.ExpenseForm;
import br.com.finances.repository.ClientRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("dev")
@TestInstance(Lifecycle.PER_CLASS)
class ExpenseControllerTestIntegration {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ClientRepository clientRepository;
	
	private TestConstructor testConstructor = new TestConstructor();
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
	}
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		testConstructor.setClient();
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
				.get("/expense?description=Description expense"))
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
				.status().isOk())
		.andDo(MockMvcResultHandlers.print());
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
		LocalDate date = LocalDate.now();
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/expense/"+ date.getYear() + "/" + date.getMonthValue()))
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
				.content(listExpenseForm.get(1).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isCreated());
	}
	
	@Test
	void shouldNotPostExpenseTwice() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/expense")
				.contentType(MediaType.APPLICATION_JSON)
				.content(listExpenseForm.get(2).toString()))
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
				.content(listExpenseForm.get(3).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotFindExpenseToUpdate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/expense/1000000")
				.contentType(MediaType.APPLICATION_JSON)
				.content(listExpenseForm.get(0).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isNotFound());
	}
	
	@Test
	void shouldNotUpdateExpense() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.put("/expense/a")
				.contentType(MediaType.APPLICATION_JSON)
				.content(listExpenseForm.get(0).toString()))
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
