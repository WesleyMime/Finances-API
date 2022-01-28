package br.com.finances.dto;

import java.math.BigDecimal;

import br.com.finances.model.Category;

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
