package br.com.finances.dto;

import br.com.finances.model.Client;

public class ClientDTO {

	private String name;
	private String email;
	
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
