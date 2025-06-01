package br.com.finances.api.summary;

import br.com.finances.api.expense.ExpenseDTO;
import br.com.finances.api.income.IncomeDTO;

import java.math.BigDecimal;
import java.util.List;

public record SummaryLastYearDTO(BigDecimal avgBalanceYear, BigDecimal percentageSavingsRate,
                                 List<BigDecimal> finalBalanceEachMonth, List<IncomeDTO> income,
                                 List<ExpenseDTO> expenses) {
}
