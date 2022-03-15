package br.com.finances.api.generic;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public abstract class GenericForm{

	@NotBlank
	private String description;
	@NotNull
	private BigDecimal value;
	@NotNull
	private LocalDate date;

	public GenericForm(String description, BigDecimal value, LocalDate date) {
		this.description = description;
		this.value = value;
		this.date = date;
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
	
	@Override
	public String toString() {
		return "{\"description\":\"" + this.description + "\", "
				+ "\"value\":\"" + this.value + "\", "
				+ "\"date\":\"" + this.date + "\"}";
	}
}
