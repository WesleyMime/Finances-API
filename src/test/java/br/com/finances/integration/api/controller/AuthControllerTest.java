package br.com.finances.integration.api.controller;

import br.com.finances.config.auth.LoginForm;
import br.com.finances.config.auth.SignForm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(Lifecycle.PER_CLASS)
public class AuthControllerTest {

	private static final String NAME = "Test";
	private static final String EMAIL = "test@email.com";
	private static final String PASSWORD = "test";
	private static final String INVALID = "";
	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	void beforeAll() throws Exception {
		mockMvc.perform(post("/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new SignForm("BeforeAll", "BeforeAll@email.com", "beforeAll").toString()));
	}

	@Test
	void shouldCreateNewClient() throws Exception {
		mockMvc.perform(post("/auth/signin")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new SignForm(NAME, EMAIL, PASSWORD).toString()))
				.andExpect(status().isCreated());
	}

	@Test
	void shouldAuthenticate() throws Exception {
		mockMvc.perform(post("/auth/login")
						.content(new LoginForm("BeforeAll@email.com", "beforeAll").toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "BeforeAll@email.com")
	void shouldAuthenticateWithUpdatedPassword() throws Exception {
		mockMvc.perform(patch("/client")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"password": "newPassword"
						}
						"""));
		mockMvc.perform(post("/auth/login")
						.content(new LoginForm("BeforeAll@email.com", "newPassword").toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mockMvc.perform(post("/auth/login")
						.content(new LoginForm("BeforeAll@email.com", "beforeAll").toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void shouldNotAuthenticate() throws Exception {
		mockMvc.perform(post("/auth/login")
						.content(new LoginForm(EMAIL, PASSWORD).toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void shouldNotFindEmailToAuthenticate() throws Exception {
		mockMvc.perform(post("/auth/login")
						.content(new LoginForm(INVALID, PASSWORD).toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void shouldNotRegisterClientWithSameEmail() throws Exception {
		mockMvc.perform(post("/auth/signin")
						.content(new SignForm(NAME, EMAIL, PASSWORD).toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	@Test
	void shouldNotRegisterClientWithEmailNotWellFormed() throws Exception {
		mockMvc.perform(post("/auth/signin")
						.content(new SignForm(NAME, INVALID, PASSWORD).toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void shouldNotRegisterClientWithoutName() throws Exception {
		mockMvc.perform(post("/auth/signin")
						.content(new SignForm(INVALID, EMAIL, PASSWORD).toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void shouldNotRegisterClientWithoutPassword() throws Exception {
		mockMvc.perform(post("/auth/signin")
						.content(new SignForm(NAME, EMAIL, INVALID).toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity());
	}
}
