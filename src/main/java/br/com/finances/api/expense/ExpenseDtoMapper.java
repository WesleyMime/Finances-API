package br.com.finances.api.expense;

import org.springframework.stereotype.Component;

import br.com.finances.api.generic.Mapper;

@Component
public class ExpenseDtoMapper implements Mapper<Expense, ExpenseDTO>{

	@Override
	public ExpenseDTO map(Expense model) {
		return new ExpenseDTO(model);
	}

}
