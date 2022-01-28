package br.com.finances.form;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.finances.model.Category;
import br.com.finances.model.Expense;

public class ExpenseForm {

	@NotBlank
	private String description;
	@NotNull
	private BigDecimal value;
	@NotNull
	private LocalDate date;	

	private Category category;

	public ExpenseForm(@NotBlank String description, @NotNull BigDecimal value, @NotNull LocalDate date, Category category) {
		this.description = description;
		this.value = value;
		this.date = date;		
		if(category == null) {
			this.category = Category.Others;
		} else {
			this.category = category;
		}
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
	public Category getCategory() {
		return category;
	}
	
	public Expense converter() {
		return new Expense(this.description, this.value, this.date, this.category);
	}
	
	public Expense update(Expense expense) {
		expense.setDescription(this.description);
		expense.setValue(this.value);
		expense.setDate(this.date);
		return expense;
	}
	
	@Override
	public String toString() {
		return "{\"description\":\"" + this.description + "\", "
				+ "\"value\":\"" + this.value + "\", "
				+ "\"date\":\"" + this.date + "\", "
				+ "\"category\":\"" + this.category + "\" }";
	}
	
	
}
