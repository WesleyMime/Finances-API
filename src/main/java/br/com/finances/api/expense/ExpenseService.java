package br.com.finances.api.expense;

import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.generic.GenericService;
import br.com.finances.api.generic.GenericServiceImpl;
import br.com.finances.config.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.function.BiFunction;

@Service
public class ExpenseService implements GenericService<Expense, ExpenseDTO, ExpenseForm> {

	private final GenericServiceImpl<Expense, ExpenseDTO, ExpenseForm> genericServiceImpl;

	public ExpenseService(
			ExpenseRepository repository, ClientRepository clientRepository,
			ExpenseDtoMapper dtoMapper, ExpenseFormMapper formMapper, CacheConfig cacheConfig) {
		this.genericServiceImpl = new GenericServiceImpl<>(repository, clientRepository, dtoMapper, formMapper,
				cacheConfig);
	}

	@Override
	public List<ExpenseDTO> getAll(String description) {
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
	public ResponseEntity<ExpenseDTO> post(ExpenseForm form, Principal principal) {
		return genericServiceImpl.post(form, principal);
	}

	@Override
	public ResponseEntity<List<ExpenseDTO>> postList(List<ExpenseForm> forms, Principal principal) {
		return genericServiceImpl.postList(forms, principal);
	}

	public ResponseEntity<ExpenseDTO> put(String id, ExpenseForm expenseForm, Principal principal) {
		return put(id, expenseForm, this::update, principal);
	}

	@Override
	public ResponseEntity<ExpenseDTO> put(String id, ExpenseForm form,
										  BiFunction<Expense, ExpenseForm, Expense> function, Principal principal) {
		return genericServiceImpl.put(id, form, function, principal);
	}

	private Expense update(Expense model, ExpenseForm form) {
		model.setDescription(form.getDescription());
		model.setValue(form.getValue());
		model.setDate(form.getDate());
		model.setCategory(form.getCategory());
		return model;
	}

	@Override
	public ResponseEntity<ExpenseDTO> delete(String id, Principal principal) {
		return genericServiceImpl.delete(id, principal);
	}

}
