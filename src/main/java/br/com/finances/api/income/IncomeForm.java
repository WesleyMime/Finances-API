package br.com.finances.api.income;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.finances.api.generic.GenericForm;

public class IncomeForm extends GenericForm{

	public IncomeForm(String description, BigDecimal value, LocalDate date) {
		super(description, value, date);
	}
	
	@Override
	public String toString() {
		return "{"
				+ "\"description\":\"" + getDescription() + "\", "
				+ "\"value\":\"" + getValue() + "\", "
				+ "\"date\":\"" + getDate() + "\" "
				+ "}";
	}
}
