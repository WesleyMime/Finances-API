package br.com.finances.api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.finances.dto.ExpenseCategoryDTO;
import br.com.finances.dto.SummaryDTO;
import br.com.finances.repository.ExpenseRepository;
import br.com.finances.repository.IncomeRepository;

@Service
public class SummaryService {

	@Autowired
	private IncomeRepository incomeRepository;
	@Autowired
	private ExpenseRepository expenseRepository;
	
	public SummaryService(IncomeRepository incomeRepository, ExpenseRepository expenseRepository) {
		this.incomeRepository = incomeRepository;
		this.expenseRepository = expenseRepository;
	}

	public ResponseEntity<SummaryDTO> getSummaryByDate(String yearString, String monthString) {
		Integer year;
		Integer month;		
		try {
			year = Integer.parseInt(yearString);
			month = Integer.parseInt(monthString);
		} catch(NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}
		
		SummaryDTO summary = new SummaryDTO();
		
		BigDecimal totalIncome = incomeRepository.totalIncomeMonth(year, month);
		BigDecimal totalExpense = expenseRepository.totalExpenseMonth(year, month);
		
		BigDecimal finalBalance = BigDecimal.ZERO;
		try {
			finalBalance = totalIncome.subtract(totalExpense);
		} catch(NullPointerException e) {
			return ResponseEntity.notFound().build();
		}
		List<ExpenseCategoryDTO> totalExpenseByCategory = expenseRepository.totalExpenseByCategory(year, month);
		
		summary.setTotalIncome(totalIncome);
		summary.setTotalExpense(totalExpense);
		summary.setFinalBalance(finalBalance);
		summary.setTotalExpenseByCategory(totalExpenseByCategory);
		
		return ResponseEntity.ok(summary);
	}
}
