package br.com.finances.api.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;

@Service
public abstract class GenericService
	<T extends GenericModel, D extends GenericDTO, F extends GenericForm> {

	private GenericRepository<T> repository;
	private ClientRepository clientRepository;
	private Mapper<T, D> dtoMapper;
	private Mapper<F, T> formMapper;

	public GenericService(
			GenericRepository<T> repository, ClientRepository clientRepository, 
			Mapper<T, D> mapper, Mapper<F, T> formMapper) {
		this.repository = repository;
		this.clientRepository = clientRepository;
		this.dtoMapper = mapper;
		this.formMapper = formMapper;
	}

	public ResponseEntity<List<D>> getAll(String description) {
		List<D> listDto = new ArrayList<>();
		List<T> listModel = new ArrayList<>();
		Client client = getClient();

		if (description == null) {
			listModel = repository.findByClient(client);
		} else {
			Optional<T> optional = repository.findByDescriptionAndClient(description, client);
			try {
				T model = optional.get();
				listModel.add(model);
			} catch (NoSuchElementException e) {
				return ResponseEntity.notFound().build();
			}
		}
		listModel.forEach(i -> {
			listDto.add(dtoMapper.map(i));
		});
		return ResponseEntity.ok(listDto);
	}

	public ResponseEntity<D> getOne(String id) {
		ResponseEntity<D> dto = tryToGetById(id);
		return dto;
	}

	public ResponseEntity<List<D>> getByDate(String yearString, String monthString) {
		Integer year;
		Integer month;
		Client client = getClient();

		try {
			year = Integer.parseInt(yearString);
			month = Integer.parseInt(monthString);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}

		List<T> listModel = repository.findByYearAndMonth(year, month, client);

		if (listModel.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<D> listDto = new ArrayList<>();
		listModel.forEach(i -> {
			listDto.add(dtoMapper.map(i));
		});

		return ResponseEntity.ok(listDto);
	}

	public ResponseEntity<D> post(F form) {
		T model = formMapper.map(form);
		
		checkIfAlreadyExists(model);
		
		Client client = getClient();
		model.setClient(client);
		
		T save = repository.save(model);
		
		D dto = dtoMapper.map(save);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	public ResponseEntity<D> put(String id, F form) {
		// Try to find by id
		ResponseEntity<D> getById = tryToGetById(id);
		if (getById.hasBody() == false) {
			// Return Bad Request or Not Found
			return getById;
		}
		T model = repository.getById(Long.parseLong(id));
		T updated = update(model, form);
		T save = repository.save(updated);
		
		D genericDto = dtoMapper.map(save);
		return ResponseEntity.ok(genericDto);
	}

	public ResponseEntity<?> delete(String id) {
		ResponseEntity<D> getById = tryToGetById(id);
		if (getById.hasBody() == false) {
			// Return Bad Request or Not Found
			return getById;
		}
		repository.deleteById(Long.parseLong(id));
		return ResponseEntity.ok().build();
	}

	private ResponseEntity<D> tryToGetById(String id) {
		Optional<T> optional = null;
		Client client = getClient();

		try {
			optional = repository.findByIdAndClient(Long.parseLong(id), client);
		} catch (NumberFormatException e) {
			// Not a number
			return ResponseEntity.badRequest().build();
		}
		T model = null;

		try {
			model = optional.get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
		D dto = dtoMapper.map(model);

		return ResponseEntity.ok(dto);
	}

	private void checkIfAlreadyExists(GenericModel model) {
		String description = model.getDescription();
		Integer monthNumber = model.getDate().getMonthValue();
		Client client = getClient();

		Optional<T> sameValue = repository.findByDescriptionAndMonth(description, monthNumber, client);
		if (sameValue.isPresent()) {
			throw new EntityExistsException();
		}
	}

	private Client getClient() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return clientRepository.findByEmail(email).get();
	}
	
	protected abstract T update(T model, F form);
}
