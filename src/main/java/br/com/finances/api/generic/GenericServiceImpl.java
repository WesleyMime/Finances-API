package br.com.finances.api.generic;

import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.config.errors.FlowAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class GenericServiceImpl
	<T extends GenericModel, S extends GenericDTO, U extends GenericForm> implements GenericService<T, S, U> {

	private final GenericRepository<T> repository;
	private final ClientRepository clientRepository;
	private final Mapper<T, S> dtoMapper;
	private final Mapper<U, T> formMapper;

	public GenericServiceImpl(
			GenericRepository<T> repository, ClientRepository clientRepository,
			Mapper<T, S> mapper, Mapper<U, T> formMapper) {
		this.repository = repository;
		this.clientRepository = clientRepository;
		this.dtoMapper = mapper;
		this.formMapper = formMapper;
	}

	public ResponseEntity<List<S>> getAll(String description) {
		Client client = getClient();

		if (description != null) {
			Optional<T> optional = repository.findByDescriptionAndClient(description, client);
			if (optional.isEmpty()) return ResponseEntity.notFound().build();

			S dto = dtoMapper.map(optional.get());
			return ResponseEntity.ok(List.of(dto));
		}
		List<S> listDto = repository.findByClient(client).stream().map(dtoMapper::map).toList();
		return ResponseEntity.ok(listDto);
	}

	public ResponseEntity<S> getOne(String id) {
		ResponseEntity<T> response = tryToGetById(id);
		if (!response.hasBody()) {
			// Return Bad Request or Not Found
			return new ResponseEntity<>(response.getStatusCode());
		}
		return ResponseEntity.ok(dtoMapper.map(response.getBody()));
	}

	public ResponseEntity<List<S>> getByDate(String yearString, String monthString) {
		int year; int month;
		Client client = getClient();

		try {
			year = Integer.parseInt(yearString);
			month = Integer.parseInt(monthString);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}
		List<T> listModel = repository.findByYearAndMonth(year, month, client);

		if (listModel.isEmpty()) return ResponseEntity.notFound().build();

		List<S> listDto = listModel.stream().map(dtoMapper::map).toList();
		return ResponseEntity.ok(listDto);
	}

	public ResponseEntity<S> post(U form) {
		T model = formMapper.map(form);
		
		checkIfAlreadyExists(model);
		
		Client client = getClient();
		model.setClient(client);
		
		T save = repository.save(model);
		
		S dto = dtoMapper.map(save);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	public ResponseEntity<S> put(String id, U form, BiFunction<T, U, T> function) {
		ResponseEntity<T> response = tryToGetById(id);
		if (!response.hasBody()) {
			// Return Bad Request or Not Found
			return new ResponseEntity<>(response.getStatusCode());
		}
		T model = response.getBody();

		T updated = function.apply(model, form);

		T save = repository.save(updated);

		S genericDto = dtoMapper.map(save);
		return ResponseEntity.ok(genericDto);
	}

	public ResponseEntity<S> delete(String id) {
		ResponseEntity<T> response = tryToGetById(id);
		if (!response.hasBody()) {
			// Return Bad Request or Not Found
			return new ResponseEntity<>(response.getStatusCode());
		}
		repository.deleteById(Long.parseLong(id));
		return ResponseEntity.ok().build();
	}

	public ResponseEntity<T> tryToGetById(String id) {
        Client client = getClient();

        long parsedLong;
        try {
            parsedLong = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
        Optional<T> optional = repository.findByIdAndClient(parsedLong, client);
        return optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

	private void checkIfAlreadyExists(GenericModel model) {
		String description = model.getDescription();
		Integer monthNumber = model.getDate().getMonthValue();
		Client client = getClient();

		Optional<T> sameValue = repository.findByDescriptionAndMonth(description, monthNumber, client);
		if (sameValue.isPresent()) {
			throw new FlowAlreadyExistsException();
		}
	}

	private Client getClient() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<Client> client = clientRepository.findByEmail(email);
		return client.orElse(null);
	}
}
