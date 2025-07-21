package br.com.finances.config.auth;

import br.com.finances.api.client.ClientDTO;
import br.com.finances.config.security.TokenDTO;
import br.com.finances.config.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authManager;

	private final TokenService tokenService;

	private final AuthenticationService authenticationService;

	public AuthController(AuthenticationManager authManager, TokenService tokenService,
						  AuthenticationService authenticationService) {
		this.authManager = authManager;
		this.tokenService = tokenService;
		this.authenticationService = authenticationService;
	}

	@PostMapping("/login")
	public ResponseEntity<TokenDTO> authenticate(@RequestBody @Valid LoginForm form) {
		return authenticationService.authenticate(form, authManager, tokenService);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<ClientDTO> signIn(@RequestBody @Valid SignForm form) {
		return authenticationService.signIn(form);		
	}

	
}
