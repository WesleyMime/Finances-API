package br.com.finances.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.finances.model.Income;

public class IncomeDTO {

	private Long id;
	private String description;
	private BigDecimal value;
	private LocalDate date;
	
	public IncomeDTO(Income income) {
		this.id = income.getId();
		this.description = income.getDescription();
		this.value = income.getValue();
		this.date = income.getDate();
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
	
	
	
}
