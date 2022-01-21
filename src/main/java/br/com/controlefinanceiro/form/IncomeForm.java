package br.com.controlefinanceiro.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.controlefinanceiro.model.Income;

public class IncomeForm {

	@NotBlank
	private String description;
	@NotNull
	private BigDecimal value;
	@NotNull
	private LocalDate date;	

	public IncomeForm(@NotBlank String description, @NotNull BigDecimal value, @NotNull LocalDate date) {
		this.description = description;
		this.value = value;
		this.date = date;
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
		return new Income(this.description, this.value, this.date);
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
