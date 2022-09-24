package br.com.finances.api.expense;

import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.generic.GenericService;
import br.com.finances.api.generic.GenericServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiFunction;

@Service
public class ExpenseService implements GenericService<Expense, ExpenseDTO, ExpenseForm> {

	private final GenericServiceImpl<Expense, ExpenseDTO, ExpenseForm> genericServiceImpl;

	public ExpenseService(
			ExpenseRepository repository, ClientRepository clientRepository, 
			ExpenseDtoMapper dtoMapper, ExpenseFormMapper formMapper) {
		this.genericServiceImpl = new GenericServiceImpl<>(repository, clientRepository, dtoMapper, formMapper);
	}

	@Override
	public ResponseEntity<List<ExpenseDTO>> getAll(String description) {
		return genericServiceImpl.getAll(description);
	}

	@Override
	public ResponseEntity<ExpenseDTO> getOne(String id) {
		return genericServiceImpl.getOne(id);
	}

	@Override
	public ResponseEntity<List<ExpenseDTO>> getByDate(String yearString, String monthString) {
		return genericServiceImpl.getByDate(yearString, monthString);
	}

	@Override
	public ResponseEntity<ExpenseDTO> post(ExpenseForm form) {
		return genericServiceImpl.post(form);
	}

	public ResponseEntity<ExpenseDTO> put(String id, ExpenseForm expenseForm) {
		return put(id, expenseForm, this::update);
	}

	@Override
	public ResponseEntity<ExpenseDTO> put(String id, ExpenseForm form, BiFunction<Expense, ExpenseForm, Expense> function) {
		return genericServiceImpl.put(id, form, function);
	}

	private Expense update(Expense model, ExpenseForm form) {
		model.setDescription(form.getDescription());
		model.setValue(form.getValue());
		model.setDate(form.getDate());
		model.setCategory(form.getCategory());
		return model;
	}

	@Override
	public ResponseEntity<ExpenseDTO> delete(String id) {
		return genericServiceImpl.delete(id);
	}

}
