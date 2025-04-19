package br.com.finances.api.summary;

import java.math.BigDecimal;
import java.util.List;

import br.com.finances.api.expense.ExpenseCategoryDTO;

public record SummaryDTO(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal finalBalance,
						 List<ExpenseCategoryDTO> totalExpenseByCategory) {
}
