package br.com.finances.integration.api.controller;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientDTO;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.Category;
import br.com.finances.api.expense.ExpenseForm;
import br.com.finances.api.expense.ExpenseService;
import br.com.finances.api.income.IncomeForm;
import br.com.finances.api.income.IncomeService;
import br.com.finances.config.auth.SignForm;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(Lifecycle.PER_CLASS)
class ClientControllerTest {

	private static final Client CLIENT = SecurityContextFactory.setClient();
	private static final ClientDTO CLIENT_DTO = new ClientDTO(CLIENT);
	private static final String ENDPOINT = "/client";
	private static final SignForm SIGN_FORM = new SignForm(
			"NewName", "newEmail@email.com", "newPassword");

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private ExpenseService expenseService;
	@Autowired
	private IncomeService incomeService;

	@BeforeAll
	void beforeAll() {
		clientRepository.deleteAll();
		clientRepository.save(CLIENT);
	}

	@BeforeEach
	void beforeEach() {
		SecurityContextFactory.setClient();
	}

	@Test
	void shouldReturnClient() throws Exception {
		incomeService.post(new IncomeForm(
				"Description", BigDecimal.TEN, LocalDate.now()));
		expenseService.post(new ExpenseForm(
				"Description", BigDecimal.TEN, LocalDate.now(), Category.Others));

		mockMvc.perform(get(ENDPOINT))
				.andExpectAll(
						jsonPath("name", is(CLIENT_DTO.getName())),
						jsonPath("email", is(CLIENT_DTO.getEmail())),
						jsonPath("password").doesNotExist(),
						status().isOk());
		mockMvc.perform(get("/income?description=d"))
				.andExpectAll(
						jsonPath("[0].description", is("Description")),
						jsonPath("[0].value", is(10.0)),
						jsonPath("[0].date", is(LocalDate.now().toString())),
						status().isOk());
		mockMvc.perform(get("/expense?description=d"))
				.andExpectAll(
						jsonPath("[0].description", is("Description")),
						jsonPath("[0].value", is(10.0)),
						jsonPath("[0].date", is(LocalDate.now().toString())),
						jsonPath("[0].category", is(Category.Others.toString())),
						status().isOk())
				.andDo(print());
	}

	@Test
	void shouldReturnForbidden() throws Exception {
		SecurityContextHolder.clearContext();
		mockMvc.perform(get(ENDPOINT))
				.andExpectAll(
						jsonPath("name").doesNotExist(),
						jsonPath("email").doesNotExist(),
						jsonPath("password").doesNotExist(),
						status().isForbidden())
				.andDo(print());
	}

	@Test
	void shouldPatchClient() throws Exception {
		mockMvc.perform(patch(ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"name": "NewName",
									"email": "newEmail@email.com",
									"password": "newPassword"
								}
								"""))
				.andExpectAll(
						jsonPath("name", is(SIGN_FORM.getName())),
						jsonPath("email", is(SIGN_FORM.getEmail())),
						jsonPath("password").doesNotExist(),
						status().isOk())
				.andDo(print());

		Client client = clientRepository.findById(CLIENT.getId()).get();
		client.setEmail("fulano@email.com");
		clientRepository.save(client);
	}

	@Test
	void shouldPatchClientWithOnlyEmail() throws Exception {
		mockMvc.perform(patch(ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								    "email": "newEmail@email.com"
								}
								"""))
				.andExpectAll(
						jsonPath("name", is(SIGN_FORM.getName())),
						jsonPath("email", is(SIGN_FORM.getEmail())),
						jsonPath("password").doesNotExist(),
						status().isOk())
				.andDo(print());

		Client client = clientRepository.findById(CLIENT.getId()).get();
		client.setEmail("fulano@email.com");
		clientRepository.save(client);
	}

	@Test
	void shouldNoPatchClientWithNoInfo() throws Exception {
		mockMvc.perform(patch(ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content(""))
				.andExpectAll(
						jsonPath("name").doesNotExist(),
						jsonPath("email").doesNotExist(),
						jsonPath("password").doesNotExist(),
						status().isBadRequest())
				.andDo(print());
	}

	@Test
	void shouldNotPatchClientWithEmptyName() throws Exception {
		mockMvc.perform(patch(ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								    "name": ""
								}
								"""))
				.andExpectAll(
						jsonPath("errors[0].field", is("name")),
						jsonPath("errors[0].detail", is("size must be between 3 and 255")),
						jsonPath("name").doesNotExist(),
						jsonPath("email").doesNotExist(),
						jsonPath("password").doesNotExist(),
						status().isUnprocessableEntity())
				.andDo(print());
	}

	@Test
	void shouldNotPatchClientWithEmptyEmail() throws Exception {
		mockMvc.perform(patch(ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								    "email": ""
								}
								"""))
				.andExpectAll(
						jsonPath("errors[0].field", is("email")),
						jsonPath("errors[0].detail", is("size must be between 5 and 255")),
						jsonPath("name").doesNotExist(),
						jsonPath("email").doesNotExist(),
						jsonPath("password").doesNotExist(),
						status().isUnprocessableEntity())
				.andDo(print());
	}

	@Test
	void shouldNotPatchClientWithMalformedEmail() throws Exception {
		mockMvc.perform(patch(ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								    "email": "emailtest.com"
								}
								"""))
				.andExpectAll(
						jsonPath("errors[0].field", is("email")),
						jsonPath("errors[0].detail", is("must be a well-formed email address")),
						jsonPath("name").doesNotExist(),
						jsonPath("email").doesNotExist(),
						jsonPath("password").doesNotExist(),
						status().isUnprocessableEntity())
				.andDo(print());
	}

	@Test
	void shouldNotPatchClientWithEmptyPassword() throws Exception {
		mockMvc.perform(patch(ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								    "password": ""
								}
								"""))
				.andExpectAll(
						jsonPath("errors[0].field", is("password")),
						jsonPath("errors[0].detail", is("size must be between 8 and 255")),
						jsonPath("name").doesNotExist(),
						jsonPath("email").doesNotExist(),
						jsonPath("password").doesNotExist(),
						status().isUnprocessableEntity())
				.andDo(print());
	}

	@Test
	void shouldReturnBadRequest() throws Exception {
		mockMvc.perform(patch(ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
						jsonPath("name").doesNotExist(),
						jsonPath("email").doesNotExist(),
						jsonPath("password").doesNotExist(),
						status().isBadRequest())
				.andDo(print());
	}

	@Test
	void shouldDeleteExpense() throws Exception {
		incomeService.post(new IncomeForm(
				"Description2", BigDecimal.TEN, LocalDate.now()));
		expenseService.post(new ExpenseForm(
				"Description2", BigDecimal.TEN, LocalDate.now(), Category.Others));

		mockMvc.perform(delete(ENDPOINT))
				.andExpectAll(
						jsonPath("name").doesNotExist(),
						jsonPath("email").doesNotExist(),
						jsonPath("password").doesNotExist(),
						status().isNoContent())
				.andDo(print());
		mockMvc.perform(get("/income?description=d"))
				.andExpectAll(
						jsonPath("[0].description").doesNotExist(),
						jsonPath("[0].value").doesNotExist(),
						jsonPath("[0].date").doesNotExist(),
						status().isNotFound());
		mockMvc.perform(get("/expense?description=d"))
				.andExpectAll(
						jsonPath("[0].description").doesNotExist(),
						jsonPath("[0].value").doesNotExist(),
						jsonPath("[0].date").doesNotExist(),
						jsonPath("[0].category").doesNotExist(),
						status().isNotFound())
				.andDo(print());
	}
}
