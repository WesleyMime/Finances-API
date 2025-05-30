package br.com.finances.api.client;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Client implements UserDetails{

	@Serial
	private static final long serialVersionUID = 7569357973771317490L;
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String email;
	private String password;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Authority> authorities = new ArrayList<>();
	
	public Client() {
	}

	public Client(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	public String getName() {
		return name;
	}
	
	public Long getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String toString() {
		return "{"
				+ "\"name\":\""+ this.name + "\","
				+ "\"email\":\""+ this.email + "\","
				+ "\"password\":\""+ this.password + "\""
				+ "}";
	}
  
}
