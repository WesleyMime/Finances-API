package br.com.finances.api.client;

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
