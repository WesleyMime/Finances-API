package br.com.finances.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.finances.api.service.TokenService;
import br.com.finances.config.security.AuthenticationService;
import br.com.finances.dto.ClientDTO;
import br.com.finances.dto.TokenDTO;
import br.com.finances.form.LoginForm;
import br.com.finances.form.SignForm;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@PostMapping("/login")
	public ResponseEntity<TokenDTO> authenticate(@RequestBody @Valid LoginForm form) {
		return authenticationService.authenticate(form, authManager, tokenService);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<ClientDTO> signIn(@RequestBody @Valid SignForm form) {
		return authenticationService.signIn(form);		
	}

	
}
