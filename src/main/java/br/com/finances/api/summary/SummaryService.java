package br.com.finances.api.summary;

import br.com.finances.api.client.Client;
import br.com.finances.api.client.ClientRepository;
import br.com.finances.api.expense.ExpenseCategoryDTO;
import br.com.finances.api.expense.ExpenseRepository;
import br.com.finances.api.income.IncomeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
public class SummaryService {

	private final IncomeRepository incomeRepository;

	private final ExpenseRepository expenseRepository;

	private final ClientRepository clientRepository;
	
	public SummaryService(IncomeRepository incomeRepository, ExpenseRepository expenseRepository, ClientRepository clientRepository) {
		this.incomeRepository = incomeRepository;
		this.expenseRepository = expenseRepository;
		this.clientRepository = clientRepository;
	}

	public ResponseEntity<SummaryDTO> getSummaryByDate(String yearString, String monthString, Principal principal) {
		Integer year; Integer month;
		Client client = getClientByEmail(principal.getName());
		
		try {
			year = Integer.parseInt(yearString);
			month = Integer.parseInt(monthString);
		} catch(NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}

		BigDecimal totalIncome = incomeRepository.totalIncomeMonth(year, month, client).orElse(BigDecimal.ZERO);
		BigDecimal totalExpense = expenseRepository.totalExpenseMonth(year, month, client).orElse(BigDecimal.ZERO);

		BigDecimal finalBalance = totalIncome.subtract(totalExpense);
		List<ExpenseCategoryDTO> totalExpenseByCategory = expenseRepository.totalExpenseByCategory(year, month, client);

		SummaryDTO summary = new SummaryDTO(totalIncome, totalExpense, finalBalance, totalExpenseByCategory);
		return ResponseEntity.ok(summary);
	}

	private Client getClientByEmail(String email) {
		return clientRepository.findByEmail(email).get();
	}
}
