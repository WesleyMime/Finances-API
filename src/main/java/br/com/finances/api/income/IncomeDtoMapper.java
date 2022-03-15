package br.com.finances.api.income;

import org.springframework.stereotype.Component;

import br.com.finances.api.generic.Mapper;

@Component
public class IncomeDtoMapper implements Mapper<Income, IncomeDTO>{

	@Override
	public IncomeDTO map(Income model) {
		return new IncomeDTO(model);
	}

}
