package br.com.finances.config.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.finances.model.Client;
import br.com.finances.repository.ClientRepository;

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
	
	
}
