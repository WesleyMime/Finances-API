package br.com.finances.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.finances.api.service.IncomeService;
import br.com.finances.form.IncomeForm;
import br.com.finances.model.Income;
import br.com.finances.repository.IncomeRepository;

class IncomeServiceTest {

	@Mock
	private IncomeRepository incomeRepository;
	
	private IncomeService incomeService;
	
	private List<Income> listIncome = generateIncome();
	
	private IncomeForm incomeForm = new IncomeForm("incomeForm description", new BigDecimal("500"), LocalDate.now());
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		
		incomeService = new IncomeService(incomeRepository);
		
		Mockito.when(incomeRepository.findAll())
		.thenReturn(listIncome);
		
		Optional<Income> optional = Optional.of(listIncome.get(0));
		Mockito.when(incomeRepository.findById(1l))
		.thenReturn(optional);
	}
	
	//GET
	
	@Test
	void shouldReturnAllIncome() {
		ResponseEntity<List<Income>> all = incomeService.getAll();
		
		assertEquals(listIncome.get(0), all.getBody().get(0));;
	}
	
	@Test
	void shouldReturnIncomeById() {
		ResponseEntity<Income> income = incomeService.getOne("1");
		assertEquals(HttpStatus.OK, income.getStatusCode());;
	}
	
	@Test
	void shouldNotReturnIncomeById() {
		ResponseEntity<Income> income = incomeService.getOne("a");
		assertEquals(HttpStatus.BAD_REQUEST, income.getStatusCode());;
	}
	
	@Test
	void shouldNotFindIncomeById() {
		ResponseEntity<Income> income = incomeService.getOne("186767587");
		assertEquals(HttpStatus.NOT_FOUND, income.getStatusCode());;
	}
	
	//POST
	
	@Test
	void shouldPostIncome() {
		ResponseEntity<Income> post = incomeService.post(incomeForm);
		assertEquals(HttpStatus.CREATED, post.getStatusCode());
	}
	
	//UPDATE
	@Test
	void shouldUpdateIncome() {
		ResponseEntity<Income> newIncome = incomeService.put("1", incomeForm);
		assertEquals(HttpStatus.OK, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotFindIncomeToUpdate() {
		ResponseEntity<Income> newIncome = incomeService.put("100000000", incomeForm);
		assertEquals(HttpStatus.NOT_FOUND, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotUpdateIncome() {
		ResponseEntity<Income> newIncome = incomeService.put("a", incomeForm);
		assertEquals(HttpStatus.BAD_REQUEST, newIncome.getStatusCode());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteIncome() {
		ResponseEntity<?> delete = incomeService.delete("1");
		assertEquals(HttpStatus.OK, delete.getStatusCode());
	}
	
	@Test
	void shouldNotFindIncomeToDelete() {
		ResponseEntity<?> delete = incomeService.delete("100000");
		assertEquals(HttpStatus.NOT_FOUND, delete.getStatusCode());
	}
	
	@Test
	void shouldNotDeleteIncome() {
		ResponseEntity<?> delete = incomeService.delete("a");
		assertEquals(HttpStatus.BAD_REQUEST, delete.getStatusCode());
	}
	
	private List<Income> generateIncome() {
		List<Income> listIncome = new ArrayList<>();
		listIncome.add(new Income("description income test", new BigDecimal("1500"), LocalDate.now()));
		listIncome.add(new Income("description test income", new BigDecimal("2500"), LocalDate.now()));
		listIncome.add(new Income("test income description", new BigDecimal("3500"), LocalDate.now()));
		return listIncome;
	}
	
}
