package br.com.finances.api.expense;

import org.springframework.stereotype.Service;

import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.generic.GenericService;

@Service
public class ExpenseService extends GenericService<Expense, ExpenseDTO, ExpenseForm>{
	
	public ExpenseService(
			ExpenseRepository repository, ClientRepository clientRepository, 
			ExpenseDtoMapper dtoMapper, ExpenseFormMapper formMapper) {
		super(repository, clientRepository, dtoMapper, formMapper);
	}

	@Override
	protected Expense update(Expense model, ExpenseForm form) {
		model.setDescription(form.getDescription());
		model.setValue(form.getValue());
		model.setDate(form.getDate());
		model.setCategory(form.getCategory());
		return model;
	}

}
