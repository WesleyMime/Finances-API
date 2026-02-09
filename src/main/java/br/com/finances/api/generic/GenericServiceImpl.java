package br.com.finances.api.generic;

import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.config.CacheEvictionService;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;

public class GenericServiceImpl
		<T extends GenericModel, S extends GenericDTO, U extends GenericForm> implements GenericService<T, S, U> {

	private final GenericRepository<T> repository;
	private final ClientRepository clientRepository;
	private final Mapper<T, S> dtoMapper;
	private final Mapper<U, T> formMapper;
	private final CacheEvictionService evictionService;

	public GenericServiceImpl(
			GenericRepository<T> repository, ClientRepository clientRepository,
			Mapper<T, S> mapper, Mapper<U, T> formMapper, CacheEvictionService evictionService) {
		this.repository = repository;
		this.clientRepository = clientRepository;
		this.dtoMapper = mapper;
		this.formMapper = formMapper;
		this.evictionService = evictionService;
	}

	public ScrollDTO<S> getAll(String description, Integer lastId, LocalDate lastDate, Principal principal) {
		Client client = getClient();
		KeysetScrollPosition position = ScrollPosition.keyset();
		if (lastId != null && lastDate != null) {
			position = ScrollPosition.of(Map.of("id", lastId, "date", lastDate), ScrollPosition.Direction.FORWARD);
		}
		Window<T> window;
		if (description != null) {
			window = repository.findFirst10ByDescriptionContainingIgnoreCaseAndClientOrderByDateDesc(description,
					client, position);
		} else {
			window = repository.findFirst10ByClientOrderByDateDesc(client, position);
		}

		List<S> list = window.stream().map(dtoMapper::map).toList();
		ScrollDTO<S> scrollDTO = new ScrollDTO<>(list, window.hasNext());
		if (window.hasNext()) {
			T last = window.getContent().getLast();
			scrollDTO.setLastId(last.getId());
			scrollDTO.setLastDate(last.getDate());
		}
		return scrollDTO;
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
		evictionService.evictCacheKeysForUser(principal.getName());

		T model = formMapper.map(form);

		Client client = getClient();
		model.setClient(client);

		T save = repository.save(model);

		S dto = dtoMapper.map(save);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	public ResponseEntity<List<S>> postList(List<U> forms, Principal principal) {
		evictionService.evictCacheKeysForUser(principal.getName());

		Client client = getClient();
		Set<T> toAddList = new HashSet<>();
		for (U form : forms) {
			T model = formMapper.map(form);
			model.setClient(client);
			toAddList.add(model);
		}
		repository.saveList(toAddList);
		List<S> dtoList = toAddList.stream().map(dtoMapper::map).toList();
		return ResponseEntity.status(HttpStatus.CREATED).body(dtoList);
	}

	public ResponseEntity<S> put(String id, U form, BiFunction<T, U, T> function, Principal principal) {
		evictionService.evictCacheKeysForUser(principal.getName());

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
		evictionService.evictCacheKeysForUser(principal.getName());

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

	private Client getClient() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<Client> client = clientRepository.findByEmail(email);
		return client.orElse(null);
	}
}
