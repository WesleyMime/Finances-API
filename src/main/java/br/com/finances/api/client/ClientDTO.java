package br.com.finances.api.client;

import java.io.Serializable;

public class ClientDTO implements Serializable {

	private final String name;
	private final String email;
	
	public ClientDTO(Client client) {
		this.name = client.getName();
		this.email = client.getUsername();
	}	
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
}
