package br.com.finances.api.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.finances.api.generic.GenericForm;

public class ExpenseForm extends GenericForm{
	
	private Category category;

	public ExpenseForm(String description, BigDecimal value, LocalDate date, Category category) {
		super(description, value, date);	
		if(category == null) {
			this.category = Category.Others;
		} else {
			this.category = category;
		}
	}
	
	public Category getCategory() {
		return category;
	}
	
	@Override
	public String toString() {
		return "{"
				+ "\"description\":\"" + getDescription() + "\", "
				+ "\"value\":\"" + getValue() + "\", "
				+ "\"date\":\"" + getDate() + "\", "
				+ "\"category\":\"" + getCategory() + "\" "
				+ "}";
	}	
}
