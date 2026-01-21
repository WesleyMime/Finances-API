package br.com.finances.api.summary;

import java.io.Serializable;
import java.math.BigDecimal;

public record SummaryBasicDTO(BigDecimal totalIncome, BigDecimal totalExpense,
							  BigDecimal balance) implements Serializable {
}