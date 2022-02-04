package br.com.finances.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthenticationService authenticationService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {		
		http.requiresChannel()	// Force the use of HTTPS
			.requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
			.requiresSecure()
		.and()
		.httpBasic().disable().formLogin().and()
		.csrf().disable()
		.authorizeRequests().anyRequest().authenticated();
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
