package br.com.finances.api.expense;

import br.com.finances.api.generic.GenericForm;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseForm extends GenericForm{

	private final Category category;

	public ExpenseForm(String description, BigDecimal value, LocalDate date, String category) {
		super(description, value, date);
		if (category == null) {
			this.category = Category.OTHERS;
		} else {
			this.category = Category.valueOf(category.toUpperCase());
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
