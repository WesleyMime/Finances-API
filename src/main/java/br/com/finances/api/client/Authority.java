package br.com.finances.api.client;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class Authority implements GrantedAuthority{

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
