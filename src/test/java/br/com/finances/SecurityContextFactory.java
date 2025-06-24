package br.com.finances;

import br.com.finances.api.client.Client;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextFactory {

	public static Client setClient() {
		SecurityContextHolder.clearContext();
		Client client = new Client("Fulano", "fulano@email.com", "password");
		UsernamePasswordAuthenticationToken authentication = 
				new UsernamePasswordAuthenticationToken(client, null, client.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return client;
	}
}
