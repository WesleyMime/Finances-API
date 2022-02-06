package br.com.finances.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Income {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String description;
	
	private BigDecimal value;
	
	private LocalDate date;
	
	@ManyToOne
	private Client client;
	
	public Income() {
	}

	public Income(String description, BigDecimal value, LocalDate date, Client client) {
		this.description = description;
		this.value = value;
		this.date = date;
		this.client = client;
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
