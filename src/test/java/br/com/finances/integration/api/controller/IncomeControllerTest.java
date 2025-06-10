package br.com.finances.integration.api.controller;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.income.Income;
import br.com.finances.api.income.IncomeForm;
import br.com.finances.api.income.IncomeRepository;
import org.hamcrest.Matchers;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(Lifecycle.PER_CLASS)
class IncomeControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private IncomeRepository incomeRepository;
	
	private static final String DESCRIPTION = "Description";
	private static final BigDecimal VALUE = new BigDecimal("1500");
	private static final LocalDate DATE = LocalDate.of(2022, 1, 1);
	private static Client CLIENT = SecurityContextFactory.setClient();
	private static Long ID;
    private static final String ENDPOINT = "/income";
		
		
	@BeforeAll
	void beforeAll() {
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
		mockMvc.perform(get(ENDPOINT))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldReturnIncomeByDescription() throws Exception {
		mockMvc.perform(get("/income?description=Description"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		mockMvc.perform(get("/income?description=description"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		mockMvc.perform(get("/income?description=cript"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldNotFindIncomeByDescription() throws Exception {
		mockMvc.perform(get("/income?description=a"))
				.andExpect(status().isNotFound());
	}	
	
	@Test
	void shouldReturnIncomeById() throws Exception {
		mockMvc.perform(get("/income/" + ID))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldNotFindIncomeById() throws Exception {
		mockMvc.perform(get("/income/87678767876"))
				.andExpect(status().isNotFound());
	}
	
	@Test
	void shouldNotReturnIncomeById() throws Exception {
		mockMvc.perform(get("/income/a"))
				.andExpect(status().isBadRequest());
	}	
	
	@Test
	void shouldFindIncomeByDate() throws Exception {
		mockMvc.perform(get("/income/2022/01"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldNotFindIncomeByDate() throws Exception {
		mockMvc.perform(get("/income/1000/01"))
				.andExpect(status().isNotFound());
	}
		
	@Test
	void shouldNotReturnIncomeByDate() throws Exception {
		mockMvc.perform(get("/income/a/a"))
				.andExpect(status().isBadRequest());
	}
	
	//POST
	
	@Test
	void shouldPostIncome() throws Exception {
		mockMvc.perform(post(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new IncomeForm("New Description", VALUE, DATE).toString()))
				.andExpect(status().isCreated());
	}
	
	@Test
	void shouldNotPostSameIncomeTwice() throws Exception {
		mockMvc.perform(post(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new IncomeForm(DESCRIPTION, VALUE, DATE).toString()))
				.andExpect(status().isConflict());
	}
	
	@Test
	void shouldNotPostIncome() throws Exception {
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
                                new IncomeForm("Income1", VALUE, DATE).toString(),
                                new IncomeForm("Income2", VALUE, DATE).toString()
                        ).toString()))
                .andExpectAll(
                        jsonPath("[0].description", is("Income1")),
                        jsonPath("[0].value", is(1500)),
                        jsonPath("[0].date", is(DATE.toString())),
                        jsonPath("[1].description", is("Income2")),
                        jsonPath("[1].value", is(1500)),
                        jsonPath("[1].date", is(DATE.toString())),
                        status().isCreated())
                .andDo(print());
		LocalDate date = LocalDate.of(2023, 1, 1);
		mockMvc.perform(post(ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content(new IncomeForm("Income1", VALUE, date).toString()))
				.andExpect(status().isCreated());
    }

    @Test
    void shouldNotPostExpenseListTwice() throws Exception {
		mockMvc.perform(post(ENDPOINT + "/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(List.of(
                                new IncomeForm("Income3", VALUE, DATE).toString(),
                                new IncomeForm("Income3", VALUE, DATE).toString()
                        ).toString()))
                .andExpectAll(
                        jsonPath("$", Matchers.hasSize(1)),
                        jsonPath("[0].description", is("Income3")),
                        jsonPath("[0].value", is(1500)),
                        jsonPath("[0].date", is(DATE.toString())),
                        status().isCreated())
                .andDo(print());
    }

    @Test
    void shouldNotPostExpenseList() throws Exception {
		mockMvc.perform(post(ENDPOINT + "/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new IncomeForm("Different income", VALUE, DATE).toString()))
                .andExpect(
                        status().isBadRequest())
                .andDo(print());
    }

	//UPDATE
	
	@Test
	void shouldUpdateIncome() throws Exception {
		mockMvc.perform(put("/income/" + ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new IncomeForm(DESCRIPTION, VALUE, DATE.plusDays(5)).toString()))
				.andExpect(status().isOk());
	}
	
	@Test
	void shouldNotFindIncomeToUpdate() throws Exception {
		mockMvc.perform(put("/income/1000000")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new IncomeForm(DESCRIPTION, VALUE, DATE).toString()))
				.andExpect(status().isNotFound());
	}
	
	@Test
	void shouldNotUpdateIncome() throws Exception {
		mockMvc.perform(put("/income/a")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new IncomeForm(DESCRIPTION, VALUE, DATE).toString()))
				.andExpect(status().isBadRequest());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteIncome() throws Exception {
		mockMvc.perform(delete("/income/" + ID))
				.andExpect(status().isOk());
		
	}
	
	@Test
	void shouldNotFindIncomeToDelete() throws Exception {
		mockMvc.perform(delete("/income/100000000000"))
				.andExpect(status().isNotFound());
		
	}
	
	@Test
	void shouldNotDeleteIncome() throws Exception {
		mockMvc.perform(delete("/income/a"))
				.andExpect(status().isBadRequest());
		
	}
	
}
