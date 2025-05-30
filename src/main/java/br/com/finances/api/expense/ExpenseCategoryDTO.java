package br.com.finances.api.expense;

import java.math.BigDecimal;
import java.util.Objects;

public record ExpenseCategoryDTO(Category category, BigDecimal totalValue) {

	public ExpenseCategoryDTO(Category category, BigDecimal totalValue) {
		this.category = Objects.requireNonNullElse(category, Category.Others);
		this.totalValue = totalValue;
	}


}
