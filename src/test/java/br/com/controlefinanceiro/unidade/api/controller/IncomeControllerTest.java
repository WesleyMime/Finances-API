package br.com.controlefinanceiro.unidade.api.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.controlefinanceiro.api.controller.IncomeController;
import br.com.controlefinanceiro.api.service.IncomeService;
import br.com.controlefinanceiro.form.IncomeForm;
import br.com.controlefinanceiro.model.Income;
import br.com.controlefinanceiro.repository.IncomeRepository;

class IncomeControllerTest {

	@Mock
	private IncomeController incomeController;
	
	@Mock
	private IncomeRepository incomeRepository;

	@Mock
	private IncomeService incomeService = new IncomeService(incomeRepository);
	
	private List<Income> income = generateIncome();
	
	private IncomeForm incomeForm = new IncomeForm("description incomeForm", new BigDecimal("500"), LocalDate.now());
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
		
		incomeController = new IncomeController(incomeRepository, incomeService);
		
		Mockito.when(incomeRepository.findAll())
		.thenReturn(income);
		
		Mockito.when(incomeRepository.save(income.get(0)))
		.thenReturn(income.get(0));
		
	}

	@Test
	void shouldReturnAllIncome() {
		ResponseEntity<List<Income>> allIncome = incomeController.getAllIncome();
		Assertions.assertTrue(allIncome.getStatusCode().is2xxSuccessful());
	}
	
	@Test
	void shouldPostIncome() {
		ResponseEntity<Income> income = incomeController.postIncome(incomeForm);		
		Assertions.assertEquals(HttpStatus.CREATED, income.getStatusCode());;
	}
	
	@Test
	void shouldUpdateIncome() {
		ResponseEntity<Income> income = incomeController.putIncome("1", incomeForm);		
		Assertions.assertNotNull(income);
	}
	
	private List<Income> generateIncome() {
		List<Income> listIncome = new ArrayList<>();
		listIncome.add(new Income("description income test", new BigDecimal("1500"), LocalDate.now()));
		listIncome.add(new Income("description test income", new BigDecimal("2500"), LocalDate.now()));
		listIncome.add(new Income("test income description", new BigDecimal("3500"), LocalDate.now()));
		return listIncome;
	}

}
