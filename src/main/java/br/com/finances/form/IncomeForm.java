package br.com.finances.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.finances.model.Client;
import br.com.finances.model.Income;

public class IncomeForm {

	@NotBlank
	private String description;
	@NotNull
	private BigDecimal value;
	@NotNull
	private LocalDate date;
	private Client client;

	public IncomeForm(String description, BigDecimal value, LocalDate date, Client client) {
		this.description = description;
		this.value = value;
		this.date = date;
		this.client = client;
	}
	public String getDescricao() {
		return description;
	}
	public BigDecimal getValor() {
		return value;
	}
	public LocalDate getData() {
		return date;
	}
	public Income converter() {
		return new Income(this.description, this.value, this.date, this.client);
	}
	
	public Income update(Income income) {
		income.setDescription(this.description);
		income.setValue(this.value);
		income.setDate(this.date);
		return income;
	}
	
	@Override
	public String toString() {
		return "{\"description\":\"" + this.description + "\", "
				+ "\"value\":\"" + this.value + "\", "
				+ "\"date\":\"" + this.date + "\"}";
	}
	
	
}
