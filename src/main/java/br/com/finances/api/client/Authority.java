package br.com.finances.api.client;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

@Entity
public class Authority implements GrantedAuthority{

	@Serial
	private static final long serialVersionUID = -3339832010368079963L;

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String authority;	
	
	public Authority() {
	}

	public Authority(Long id, String authority) {
		this.id = id;
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	public Long getId() {
		return id;
	}

	
	
}
