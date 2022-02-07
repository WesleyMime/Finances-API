package br.com.finances.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@Profile("dev")
public class DevSecurityConfigurations extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {		
		http.requiresChannel()	// Force the use of HTTPS
				.requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
				.requiresSecure()
		.and()
			.authorizeRequests()
				.anyRequest().permitAll()
		.and()
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
	}
}
