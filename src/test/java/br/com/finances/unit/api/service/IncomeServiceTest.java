package br.com.finances.unit.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.finances.SecurityContextFactory;
import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.income.Income;
import br.com.finances.api.income.IncomeDTO;
import br.com.finances.api.income.IncomeDtoMapper;
import br.com.finances.api.income.IncomeForm;
import br.com.finances.api.income.IncomeFormMapper;
import br.com.finances.api.income.IncomeRepository;
import br.com.finances.api.income.IncomeService;

class IncomeServiceTest {

	@Mock
	private IncomeRepository incomeRepository;
	@Mock
	private ClientRepository clientRepository;
	
	private IncomeDtoMapper dtoMapper = new IncomeDtoMapper();

	private IncomeFormMapper formMapper = new IncomeFormMapper();
	
 	private IncomeService incomeService;
	
	private static final String DESCRIPTION = "description";
	private static final BigDecimal VALUE = new BigDecimal("1500");
	private static final LocalDate DATE = LocalDate.of(2022, 01, 01);
	private static final Client CLIENT = SecurityContextFactory.setClient();
	
	private static final IncomeForm FORM = new IncomeForm(DESCRIPTION, VALUE, DATE);
	private static final Income INCOME = new Income(DESCRIPTION, VALUE, DATE);
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		
		this.incomeService = new IncomeService(incomeRepository, clientRepository, dtoMapper, formMapper);
		
		when(clientRepository.findByEmail(anyString()))
		.thenReturn(Optional.of(CLIENT));
		
		when(incomeRepository.save(any()))
		.thenReturn(INCOME);
	}
	
	//GET
	
	@Test
	void shouldReturnAllIncome() {
		when(incomeRepository.findByClient(CLIENT))
		.thenReturn(List.of(INCOME));
		ResponseEntity<List<IncomeDTO>> all = incomeService.getAll(null);	
		assertEquals(DESCRIPTION, all.getBody().get(0).getDescription());
	}
	
	@Test
	void shouldReturnIncomeByDescription() {
		when(incomeRepository.findByDescriptionAndClient(any(), any()))
		.thenReturn(Optional.of(INCOME));
		ResponseEntity<List<IncomeDTO>> all = incomeService.getAll(DESCRIPTION);		
		assertEquals(DESCRIPTION, all.getBody().get(0).getDescription());
	}
	
	@Test
	void shouldNotFindIncomeByDescription() {
		ResponseEntity<List<IncomeDTO>> all = incomeService.getAll("");		
		assertEquals(HttpStatus.NOT_FOUND, all.getStatusCode());
	}
	
	@Test
	void shouldReturnIncomeById() {
		when(incomeRepository.findByIdAndClient(any(), any()))
		.thenReturn(Optional.of(INCOME));
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
		when(incomeRepository.findByYearAndMonth(any(), any(), any()))
		.thenReturn(List.of(INCOME));
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
		ResponseEntity<IncomeDTO> post = incomeService.post(FORM);
		assertEquals(HttpStatus.CREATED, post.getStatusCode());
	}
	
	//UPDATE
	@Test
	void shouldUpdateIncome() {
		when(incomeRepository.findByIdAndClient(any(), any()))
		.thenReturn(Optional.of(INCOME));
		when(incomeRepository.getById(any()))
		.thenReturn(INCOME);
		ResponseEntity<IncomeDTO> newIncome = incomeService.put("1", FORM);
		assertEquals(HttpStatus.OK, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotFindIncomeToUpdate() {
		ResponseEntity<IncomeDTO> newIncome = incomeService.put("100000000", FORM);
		assertEquals(HttpStatus.NOT_FOUND, newIncome.getStatusCode());
	}
	
	@Test
	void shouldNotUpdateIncome() {
		ResponseEntity<IncomeDTO> newIncome = incomeService.put("a", FORM);
		assertEquals(HttpStatus.BAD_REQUEST, newIncome.getStatusCode());
	}
	
	//DELETE
	
	@Test
	void shouldDeleteIncome() {
		when(incomeRepository.findByIdAndClient(any(), any()))
		.thenReturn(Optional.of(INCOME));
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
	
}
