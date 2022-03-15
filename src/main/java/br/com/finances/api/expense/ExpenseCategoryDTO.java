package br.com.finances.api.expense;

import java.math.BigDecimal;

public class ExpenseCategoryDTO {

	private Category category;
	private BigDecimal totalValue;
	
	public ExpenseCategoryDTO(Category category, BigDecimal totalValue) {
		if(category == null) {
			this.category = Category.Others;
		} else {
			this.category = category;
		}
		this.totalValue = totalValue;
	}

	public Category getCategory() {
		return category;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}
	
	
}
