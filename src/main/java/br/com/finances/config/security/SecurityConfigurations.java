package br.com.finances.config.security;

import br.com.finances.api.client.ClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

	private final TokenService tokenService;

	private final ClientRepository clientRepository;

	public SecurityConfigurations(TokenService tokenService, ClientRepository clientRepository) {
		this.tokenService = tokenService;
		this.clientRepository = clientRepository;
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder,
											 UserDetailsService userDetailsService) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
		authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.cors(cors ->
						cors.configurationSource(apiConfigurationSource()))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/auth/*").permitAll()
						.requestMatchers("/error").permitAll()
						.anyRequest().authenticated()
				)
				.csrf(AbstractHttpConfigurer::disable
				)
				.sessionManagement(configure -> configure
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.addFilterBefore(new TokenAuthenticationFilter(tokenService, clientRepository), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	private CorsConfigurationSource apiConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("https://marujo.site", "http://localhost:4200"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of("Authorization"));
		configuration.setMaxAge(3600L);
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
