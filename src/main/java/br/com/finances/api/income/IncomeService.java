package br.com.finances.api.income;

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
public class IncomeService implements GenericService<Income, IncomeDTO, IncomeForm> {

	private final GenericServiceImpl<Income, IncomeDTO, IncomeForm> genericServiceImpl;

	public IncomeService(
			IncomeRepository repository, ClientRepository clientRepository,
			IncomeDtoMapper dtoMapper, IncomeFormMapper formMapper, CacheConfig cacheConfig) {
		this.genericServiceImpl = new GenericServiceImpl<>(repository, clientRepository, dtoMapper, formMapper,
				cacheConfig);
	}

	@Override
	public List<IncomeDTO> getAll(String description) {
		return genericServiceImpl.getAll(description);
	}

	@Override
	public ResponseEntity<IncomeDTO> getOne(String id) {
		return genericServiceImpl.getOne(id);
	}

	@Override
	public ResponseEntity<List<IncomeDTO>> getByDate(String yearString, String monthString) {
		return genericServiceImpl.getByDate(yearString, monthString);
	}

	@Override
	public ResponseEntity<IncomeDTO> post(IncomeForm form, Principal principal) {
		return genericServiceImpl.post(form, principal);
	}

	@Override
	public ResponseEntity<List<IncomeDTO>> postList(List<IncomeForm> forms, Principal principal) {
		return genericServiceImpl.postList(forms, principal);
	}

	public ResponseEntity<IncomeDTO> put(String id, IncomeForm incomeForm, Principal principal) {
		return put(id, incomeForm, this::update, principal);
	}

	@Override
	public ResponseEntity<IncomeDTO> put(String id, IncomeForm form, BiFunction<Income, IncomeForm, Income> function,
										 Principal principal) {
		return genericServiceImpl.put(id, form, function, principal);
	}

	private Income update(Income model, IncomeForm form) {
		model.setDescription(form.getDescription());
		model.setValue(form.getValue());
		model.setDate(form.getDate());
		return model;
	}

	@Override
	public ResponseEntity<IncomeDTO> delete(String id, Principal principal) {
		return genericServiceImpl.delete(id, principal);
	}

}

