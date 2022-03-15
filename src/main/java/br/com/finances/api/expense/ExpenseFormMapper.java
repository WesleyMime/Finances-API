package br.com.finances.api.expense;

import org.springframework.stereotype.Component;

import br.com.finances.api.generic.Mapper;

@Component
public class ExpenseFormMapper implements Mapper<ExpenseForm, Expense> {

	@Override
	public Expense map(ExpenseForm source) {
		return new Expense(source.getDescription(), source.getValue(), source.getDate(), source.getCategory());
	}
}
