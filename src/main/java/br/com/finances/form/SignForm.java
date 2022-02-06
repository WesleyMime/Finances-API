package br.com.finances.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.finances.model.Client;

public class SignForm {
	
	@NotBlank
	private String name;

	@NotBlank @Email
	private String email;
	
	@NotBlank
	private String password;
	
	public SignForm(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}	
	
	public Client converter() {
		return new Client(this.name, this.email, new BCryptPasswordEncoder().encode(this.password));
	}
	
}