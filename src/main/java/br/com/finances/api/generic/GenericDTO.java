package br.com.finances.api.generic;

import br.com.finances.api.client.ClientDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GenericDTO implements Serializable {

	private Long id;
	private String description;
	private BigDecimal value;
	private LocalDate date;
	private ClientDTO client;
	
	public GenericDTO(GenericModel model) {
		this.id = model.getId();
		this.description = model.getDescription();
		this.value = model.getValue();
		this.date = model.getDate();
		try {
			this.client = new ClientDTO(model.getClient());
		} catch(NullPointerException e) {
			this.client = null;
		}
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getValue() {
		return value;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public ClientDTO getClient() {
		return client;
	}
}
