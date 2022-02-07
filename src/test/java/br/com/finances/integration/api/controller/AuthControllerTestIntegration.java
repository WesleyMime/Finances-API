package br.com.finances.integration.api.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import br.com.finances.form.LoginForm;
import br.com.finances.form.SignForm;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("prod")
public class AuthControllerTestIntegration {

	@Autowired
	private MockMvc mockMvc;
	
	private TestConstructor testConstructor = new TestConstructor();
	
	private List<SignForm> listSignInForm = testConstructor.generateSignForm();
	private List<LoginForm> listLoginForm = testConstructor.generateLoginForm();
	
	@BeforeEach
	void beforeEach() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(listSignInForm.get(5).toString()));
	}
	
	@Test
	void shouldCreateNewClient() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(listSignInForm.get(0).toString()))
		.andExpect(MockMvcResultMatchers
				.status().isCreated());
	}
	
	@Test
	void shouldAuthenticate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/login")
				.content(listLoginForm.get(1).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isOk());
	}
	
	@Test
	void shouldNotAuthenticate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/login")
				.content(listLoginForm.get(2).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotFindEmailToAuthenticate() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/login")
				.content(listLoginForm.get(5).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithSameEmail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(listSignInForm.get(0).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithEmailNotWellFormed() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(listSignInForm.get(2).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithoutName() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(listSignInForm.get(1).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
	
	@Test
	void shouldNotRegisterClientWithoutPassword() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
				.post("/auth/signin")
				.content(listSignInForm.get(4).toString())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status().isBadRequest());
	}
}
