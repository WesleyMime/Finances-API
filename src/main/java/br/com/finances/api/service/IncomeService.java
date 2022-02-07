package br.com.finances.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.finances.dto.IncomeDTO;
import br.com.finances.form.IncomeForm;
import br.com.finances.model.Client;
import br.com.finances.model.Income;
import br.com.finances.repository.IncomeRepository;

@Service
public class IncomeService {
	
	@Autowired
	private IncomeRepository incomeRepository;
	
	public IncomeService(IncomeRepository incomeRepository) {
		this.incomeRepository = incomeRepository;
	}

	public ResponseEntity<List<IncomeDTO>> getAll(String description) {
		List<IncomeDTO> listIncomeDto = new ArrayList<>(); 
		List<Income> listIncome = new ArrayList<>();
		Client client = getClient();

		if(description == null) {
			listIncome = incomeRepository.findByClient(client);
		} else {
			Optional<Income> optionalIncome = incomeRepository.findByDescriptionAndClient(description, client);
			try {
				Income income = optionalIncome.get();				
				listIncome.add(income);
			} catch(NoSuchElementException e) {
				return ResponseEntity.notFound().build();
			}
		}
		listIncome.forEach(i -> {
			listIncomeDto.add(new IncomeDTO(i));
		});		
		return ResponseEntity.ok(listIncomeDto);
	}

	public ResponseEntity<IncomeDTO> getOne(String id) {
		ResponseEntity<IncomeDTO> incomeDto = tryToGetById(id);		
		return incomeDto;
	}
	
	public ResponseEntity<List<IncomeDTO>> getByDate(String yearString, String monthString) {
		Integer year;
		Integer month;
		Client client = getClient();
		
		try {
			year = Integer.parseInt(yearString);
			month = Integer.parseInt(monthString);
		} catch(NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}
		
		List<IncomeDTO> listIncomeDto = new ArrayList<>(); 
		
		List<Income> listIncome = incomeRepository.findByYearAndMonth(year, month, client);
		
		if(listIncome.isEmpty()) {			
			return ResponseEntity.notFound().build();
		}
		
		listIncome.forEach(i -> {
			listIncomeDto.add(new IncomeDTO(i));
		});
		
		return ResponseEntity.ok(listIncomeDto);
	}

	public ResponseEntity<IncomeDTO> post(IncomeForm form) {
		Income income = form.converter();
		
		checkIfAlreadyExists(income);
		Client client = getClient();
		income.setClient(client);
		Income save = incomeRepository.save(income);
		IncomeDTO incomeDto = new IncomeDTO(save);
		return ResponseEntity.status(HttpStatus.CREATED).body(incomeDto);
	}
	
	public ResponseEntity<IncomeDTO> put(String id, IncomeForm form) {
		
		// Try to find by id
		ResponseEntity<IncomeDTO> getById = tryToGetById(id);
		if(getById.hasBody() == false) {
			// Return Bad Request or Not Found
			return getById;
		}		
		Income income = incomeRepository.getById(Long.parseLong(id));
		Income updated = form.update(income);
		
		Income save = incomeRepository.save(updated);
		IncomeDTO incomeDto = new IncomeDTO(save);
		return ResponseEntity.ok(incomeDto);
	}

	public ResponseEntity<?> delete(String id) {
		ResponseEntity<IncomeDTO> getById = tryToGetById(id);
		if(getById.hasBody() == false) {
			// Return Bad Request or Not Found
			return getById;
		}
		incomeRepository.deleteById(Long.parseLong(id));
		return ResponseEntity.ok().build();
	}
	
	private ResponseEntity<IncomeDTO> tryToGetById(String id) {
		Optional<Income> incomeOptional = null;
		Client client = getClient();
		
		try {
			incomeOptional = incomeRepository.findByIdAndClient(Long.parseLong(id), client);
		} catch(NumberFormatException e) {
			// Not a number
			return ResponseEntity.badRequest().build();
		}		
		Income income = null;
		
		try {
			income = incomeOptional.get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
		IncomeDTO incomeDTO = new IncomeDTO(income);
		
		return ResponseEntity.ok(incomeDTO);
	}
	
	private void checkIfAlreadyExists(Income income) {		
		String description = income.getDescription();
		Integer monthNumber = income.getDate().getMonthValue();
		Client client = getClient();
		
		Optional<Income> sameIncome = incomeRepository.findByDescriptionAndMonth(description, monthNumber, client);
		if (sameIncome.isPresent()) {
			throw new EntityExistsException();
		}
	}
	
	private Client getClient() {
		Client client = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return client;
	}

}
