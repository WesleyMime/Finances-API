package br.com.finances.integration.api.controller;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.finances.config.security.auth.LoginForm;
import br.com.finances.config.security.auth.SignForm;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("prod")
@TestInstance(Lifecycle.PER_CLASS)
public class AuthControllerTestIntegration {

	@Autowired
	private MockMvc mockMvc;
	
	private static final String NAME = "Test"; 
	private static final String EMAIL = "test@email.com"; 
	private static final String PASSWORD = "test";
	private static final String INVALID = "";
	
	@BeforeAll
	void beforAll() throws Exception {		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new SignForm("BeforeAll", "BeforeAll@email.com", "beforeAll").toString()));
	}
	
	@Test
	void shouldCreateNewClient() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new SignForm(NAME, EMAIL, PASSWORD).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isCreated());
	}
	
	@Test
	void shouldAuthenticate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/login")
				.content(new LoginForm("BeforeAll@email.com", "beforeAll").toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotAuthenticate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/login")
				.content(new LoginForm(EMAIL, PASSWORD).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotFindEmailToAuthenticate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/login")
				.content(new LoginForm(INVALID, PASSWORD).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithSameEmail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(new SignForm(NAME, EMAIL, PASSWORD).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithEmailNotWellFormed() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(new SignForm(NAME, INVALID, PASSWORD).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithoutName() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(new SignForm(INVALID, EMAIL, PASSWORD).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithoutPassword() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(new SignForm(NAME, EMAIL, INVALID).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
}
