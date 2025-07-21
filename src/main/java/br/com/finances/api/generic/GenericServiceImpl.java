package br.com.finances.api.generic;

import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.config.CacheConfig;
import br.com.finances.config.errors.FlowAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class GenericServiceImpl
		<T extends GenericModel, S extends GenericDTO, U extends GenericForm> implements GenericService<T, S, U> {

	private final GenericRepository<T> repository;
	private final ClientRepository clientRepository;
	private final Mapper<T, S> dtoMapper;
	private final Mapper<U, T> formMapper;
	private final CacheConfig cacheConfig;

	public GenericServiceImpl(
			GenericRepository<T> repository, ClientRepository clientRepository,
			Mapper<T, S> mapper, Mapper<U, T> formMapper, CacheConfig cacheConfig) {
		this.repository = repository;
		this.clientRepository = clientRepository;
		this.dtoMapper = mapper;
		this.formMapper = formMapper;
		this.cacheConfig = cacheConfig;
	}

	public List<S> getAll(String description) {
		Client client = getClient();

		if (description != null) {
			List<T> list = repository.findByDescriptionContainingIgnoreCaseAndClient(description,
					client);
			if (list.isEmpty()) return List.of();

			return list.stream().map(dtoMapper::map).toList();
		}
		return repository.findByClient(client).stream().map(dtoMapper::map).toList();
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
		int year;
		int month;
		Client client = getClient();

		try {
			year = Integer.parseInt(yearString);
			month = Integer.parseInt(monthString);
		} catch (NumberFormatException _) {
			return ResponseEntity.badRequest().build();
		}
		List<T> listModel = repository.findByYearAndMonth(year, month, client);

		if (listModel.isEmpty()) return ResponseEntity.notFound().build();

		List<S> listDto = listModel.stream().map(dtoMapper::map).toList();
		return ResponseEntity.ok(listDto);
	}

	public ResponseEntity<S> post(U form, Principal principal) {
		cacheConfig.evictClientCache(principal);

		T model = formMapper.map(form);
		checkIfAlreadyExists(model);

		Client client = getClient();
		model.setClient(client);

		T save = repository.save(model);

		S dto = dtoMapper.map(save);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	public ResponseEntity<List<S>> postList(List<U> forms, Principal principal) {
		cacheConfig.evictClientCache(principal);

		Client client = getClient();
		Set<T> toAddList = new HashSet<>();
		for (U form : forms) {
			T model = formMapper.map(form);
			try {
				checkIfAlreadyExists(model);
			} catch (FlowAlreadyExistsException _) {
				continue;
			}
			model.setClient(client);
			toAddList.add(model);
		}
		repository.saveList(toAddList);
		List<S> dtoList = toAddList.stream().map(dtoMapper::map).toList();
		return ResponseEntity.status(HttpStatus.CREATED).body(dtoList);
	}

	public ResponseEntity<S> put(String id, U form, BiFunction<T, U, T> function, Principal principal) {
		cacheConfig.evictClientCache(principal);

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

	public ResponseEntity<S> delete(String id, Principal principal) {
		cacheConfig.evictClientCache(principal);

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
		} catch (NumberFormatException _) {
			return ResponseEntity.badRequest().build();
		}
		Optional<T> optional = repository.findByIdAndClient(parsedLong, client);
		return optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	private void checkIfAlreadyExists(GenericModel model) {
		String description = model.getDescription();
		Integer year = model.getDate().getYear();
		Integer month = model.getDate().getMonthValue();
		Client client = getClient();

		Optional<T> sameValue = repository.findByDescriptionAndDate(description, year, month,
				client);
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
