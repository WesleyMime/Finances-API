package br.com.finances.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.finances.api.client.ClientRepository;
import br.com.finances.config.auth.AuthenticationService;

@Configuration
@EnableWebSecurity
@Profile("prod")
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthenticationService authenticationService;	
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {		
		http.requiresChannel()	// Force the use of HTTPS
				.requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
				.requiresSecure()
		.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/auth/*").permitAll()
				.antMatchers(HttpMethod.GET, "/error").permitAll()
				.anyRequest().authenticated()
		.and()
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
			.addFilterBefore(new TokenAuthenticationFilter(tokenService, clientRepository), UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(authenticationService).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
	}
	
}
