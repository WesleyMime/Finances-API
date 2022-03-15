package br.com.finances.api.income;

import org.springframework.stereotype.Service;

import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.generic.GenericService;

@Service
public class IncomeService extends GenericService<Income, IncomeDTO, IncomeForm> {

	public IncomeService(
			IncomeRepository repository, ClientRepository clientRepository, 
			IncomeDtoMapper dtoMapper, IncomeFormMapper formMapper) {
		super(repository, clientRepository, dtoMapper, formMapper);
	}

	@Override
	protected Income update(Income model, IncomeForm form) {
		model.setDescription(form.getDescription());
		model.setValue(form.getValue());
		model.setDate(form.getDate());
		return model;
	}		

}

