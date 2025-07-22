package br.com.finances.api.generic;

import br.com.finances.api.client.Client;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@MappedSuperclass
public abstract class GenericModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String description;

	private BigDecimal value;

	private LocalDate date;

	@ManyToOne()
	private Client client;

	protected GenericModel() {
	}

	protected GenericModel(String description, BigDecimal value, LocalDate date) {
		this.description = description;
		this.value = value;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GenericModel that)) return false;
		return Objects.equals(description, that.description) && Objects.equals(
				getYearAndMonth(date),
				getYearAndMonth(that.date)) && Objects.equals(client, that.client);
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, getYearAndMonth(date), client);
	}

	private LocalDate getYearAndMonth(LocalDate date) {
		return LocalDate.of(date.getYear(), date.getMonthValue(), 15);
	}
}
