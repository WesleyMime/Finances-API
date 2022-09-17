package br.com.finances.config.security;

import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter{

	private final TokenService tokenService;
	private final ClientRepository clientRepository;
	
	public TokenAuthenticationFilter(TokenService tokenService, ClientRepository clientRepository) {
		this.tokenService = tokenService;
		this.clientRepository = clientRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = getToken(request);
		
		boolean valid = tokenService.isValid(token);
		if(valid) {
			authenticate(token);
		}
		
		
		filterChain.doFilter(request, response);
		
	}

	private String getToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if(token == null || !token.startsWith("Bearer ")) {
			return null;
		}
		return token.substring(7);
	}
	
	private void authenticate(String token) {
		Long idClient = tokenService.getIdClient(token);
		Client client = clientRepository.findById(idClient).get();
		
		UsernamePasswordAuthenticationToken authentication = 
				new UsernamePasswordAuthenticationToken(client, null, client.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
