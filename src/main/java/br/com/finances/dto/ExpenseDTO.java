package br.com.finances.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.finances.model.Expense;

public class ExpenseDTO {

	private Long id;
	private String description;
	private BigDecimal value;
	private LocalDate date;
	
	public ExpenseDTO(Expense expense) {
		this.id = expense.getId();
		this.description = expense.getDescription();
		this.value = expense.getValue();
		this.date = expense.getDate();
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
