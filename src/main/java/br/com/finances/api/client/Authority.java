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

	private String name;
	
	public Authority() {
	}

	public Authority(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String getAuthority() {
		return name;
	}

	public Long getId() {
		return id;
	}

	
	
}
