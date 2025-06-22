package br.com.finances.api.generic;

import br.com.finances.api.client.Client;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@MappedSuperclass
public abstract class GenericModel{

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String description;
	
	private BigDecimal value;
	
	private LocalDate date;

    @ManyToOne()
	private Client client;
	
	public GenericModel() {
	}

	public GenericModel(String description, BigDecimal value, LocalDate date) {
		this.description = description;
		this.value = value;
		this.date = date;
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

	public Client getClient() {
		return client;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}

}
