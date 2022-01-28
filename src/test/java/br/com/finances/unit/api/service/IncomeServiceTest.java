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
import br.com.finances.dto.IncomeDTO;
import br.com.finances.form.IncomeForm;
import br.com.finances.model.Income;
import br.com.finances.repository.IncomeRepository;

class IncomeServiceTest {

	@Mock
	private IncomeRepository incomeRepository;
	
	private IncomeService incomeService;
	
	private List<Income> listIncome = generateIncome();
	
	private IncomeForm incomeForm = new IncomeForm("incomeForm description", new BigDecimal("500"), LocalDate.now());
	private IncomeDTO incomeDto = new IncomeDTO(listIncome.get(2));
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		
		incomeService = new IncomeService(incomeRepository);
		
		Mockito.when(incomeRepository.findAll())
		.thenReturn(listIncome);
		
		Optional<Income> optional = Optional.of(listIncome.get(0));
		Mockito.when(incomeRepository.findById(1l))
		.thenReturn(optional);
		
		Mockito.when(incomeRepository.save(Mockito.any()))
		.thenReturn(listIncome.get(0));
		
		Mockito.when(incomeRepository.getById(1l))
		.thenReturn(listIncome.get(0));
		
		Mockito.when(incomeRepository.findByDescription("description income test"))
		.thenReturn(optional);
		
		Mockito.when(incomeRepository.findByDescription("a"))
		.thenReturn(Optional.ofNullable(null));
		
		Mockito.when(incomeRepository.findByYearAndMonth(Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(listIncome);
		
		Mockito.when(incomeRepository.findByYearAndMonth(1000, 01))
		.thenReturn(new ArrayList<>());
	}
	
	//GET
	
	@Test
	void shouldReturnAllIncome() {
		ResponseEntity<List<IncomeDTO>> all = incomeService.getAll(null);
		
		assertEquals(incomeDto.getDescription(), all.getBody().get(2).getDescription());
	}
	
	@Test
	void shouldReturnIncomeByDescription() {
		ResponseEntity<List<IncomeDTO>> all = incomeService.getAll("description income test");
		
		assertEquals(listIncome.get(0).getDescription(), all.getBody().get(0).getDescription());
	}
	
	@Test
	void shouldNotFindIncomeByDescription() {
		ResponseEntity<List<IncomeDTO>> all = incomeService.getAll("");		
		assertEquals(HttpStatus.NOT_FOUND, all.getStatusCode());
	}
	
	@Test
	void shouldReturnIncomeById() {
		ResponseEntity<IncomeDTO> income = incomeService.getOne("1");
		assertEquals(HttpStatus.OK, income.getStatusCode());
	}
	
	@Test
	void shouldNotReturnIncomeById() {
		ResponseEntity<IncomeDTO> income = incomeService.getOne("a");
		assertEquals(HttpStatus.BAD_REQUEST, income.getStatusCode());
	}
	
	@Test
	void shouldNotFindIncomeById() {
		ResponseEntity<IncomeDTO> income = incomeService.getOne("186767587");
		assertEquals(HttpStatus.NOT_FOUND, income.getStatusCode());
	}
	
	@Test
	void shouldReturnIncomeByDate() {
		ResponseEntity<List<IncomeDTO>> income = incomeService.getByDate("2022", "01");
		assertEquals(HttpStatus.OK, income.getStatusCode());
	}
	
	@Test
	void shouldNotReturnIncomeByDate() {
		ResponseEntity<List<IncomeDTO>> income = incomeService.getByDate("a", "a");
		assertEquals(HttpStatus.BAD_REQUEST, income.getStatusCode());
	}
	
	@Test
	void shouldNotFindIncomeByDate() {
		ResponseEntity<List<IncomeDTO>> income = incomeService.getByDate("1000", "01");
		assertEquals(HttpStatus.NOT_FOUND, income.getStatusCode());
	}
	
	
	//POST
	
	@Test
	void shouldPostIncome() {
		ResponseEntity<IncomeDTO> post = incomeService.post(incomeForm);
		assertEquals(HttpStatus.CREATED, post.getStatusCode());
	}
	
	//UPDATE
	@Test
	void shouldUpdateIncome() {
		ResponseEntity<IncomeDTO> newIncome = incomeService.put("1", incomeForm);
		assertEquals(HttpStatus.OK, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotFindIncomeToUpdate() {
		ResponseEntity<IncomeDTO> newIncome = incomeService.put("100000000", incomeForm);
		assertEquals(HttpStatus.NOT_FOUND, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotUpdateIncome() {
		ResponseEntity<IncomeDTO> newIncome = incomeService.put("a", incomeForm);
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
