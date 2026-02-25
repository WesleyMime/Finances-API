package br.com.finances.api.summary;

import br.com.finances.api.expense.ExpenseDTO;
import br.com.finances.api.income.IncomeDTO;

import java.util.List;

public record RecentTransactionsDTO(List<IncomeDTO> income, List<ExpenseDTO> expense) {
}
