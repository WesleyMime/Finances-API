package br.com.finances.integration.api.controller;

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

import br.com.finances.form.LoginForm;
import br.com.finances.form.SignForm;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AuthControllerTestIntegration {

	@Autowired
	private MockMvc mockMvc;
	
	private SignForm signinForm = new SignForm("Test", "test@email.com", "test");
	private SignForm signinForm2 = new SignForm("Test", "test", "test");
	private SignForm signinForm3 = new SignForm("", "test@email.com", "test");
	private SignForm signinForm4 = new SignForm("Test", "test@email.com", "");
	private LoginForm loginForm = new LoginForm("test@email.com", "test");
	private LoginForm loginForm2 = new LoginForm("test2@email.com", "test");
	private LoginForm loginForm3 = new LoginForm("test@email.com", "test3");
	
	@BeforeEach
	void beforeEach() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(signinForm.toString()));
	}
	
	@Test
	void shouldAuthenticate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/login")
				.content(loginForm.toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotAuthenticate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/login")
				.content(loginForm2.toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotFindEmailToAuthenticate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/login")
				.content(loginForm3.toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithSameEmail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(signinForm.toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithEmailNotWellFormed() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(signinForm2.toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithoutName() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(signinForm3.toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithoutPassword() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(signinForm4.toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
}
