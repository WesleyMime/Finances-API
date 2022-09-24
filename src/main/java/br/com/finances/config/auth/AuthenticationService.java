package br.com.finances.config.auth;

import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientDTO;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.config.errors.EmailAlreadyRegisteredException;
import br.com.finances.config.security.TokenDTO;
import br.com.finances.config.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService{

	@Autowired
	private ClientRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Client> client = repository.findByEmail(username);
		if (client.isEmpty()) {
			throw new UsernameNotFoundException("Email doesn't exists in the database."); 
		}
		
		return client.get();
	}
	public ResponseEntity<TokenDTO> authenticate(LoginForm form, AuthenticationManager authManager, TokenService tokenService) {
		UsernamePasswordAuthenticationToken login = form.converter();
		try {
			Authentication authentication = authManager.authenticate(login);
			String token = tokenService.generateToken(authentication);
			return ResponseEntity.ok(new TokenDTO(token, "Bearer"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}
	}
	
	public ResponseEntity<ClientDTO> signIn(SignForm form) {
		Client client = form.converter();			
		checkIfAlreadyExists(client);			
		ClientDTO clientDto = new ClientDTO(repository.save(client));
		return ResponseEntity.status(HttpStatus.CREATED).body(clientDto);
	}
	
	public void checkIfAlreadyExists(Client client) {
		Optional<Client> optional = repository.findByEmail(client.getUsername());
		if(optional.isPresent()) {
			throw new EmailAlreadyRegisteredException();
		}
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
