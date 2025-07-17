package br.com.finances.api.summary;

import br.com.finances.api.expense.ExpenseCategoryDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record SummaryDTO(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal finalBalance,
						 List<ExpenseCategoryDTO> totalExpenseByCategory) implements Serializable {
}
