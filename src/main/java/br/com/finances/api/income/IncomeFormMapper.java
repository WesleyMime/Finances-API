package br.com.finances.api.income;

import org.springframework.stereotype.Component;

import br.com.finances.api.generic.Mapper;

@Component
public class IncomeFormMapper implements Mapper<IncomeForm, Income> {

	@Override
	public Income map(IncomeForm source) {
		return new Income(source.getDescription(), source.getValue(), source.getDate());
	}

	public Income update(Income income, IncomeForm form) {
		income.setDescription(form.getDescription());
		income.setValue(form.getValue());
		income.setDate(form.getDate());
		
		return income;
	}
}
