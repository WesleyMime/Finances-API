package br.com.finances.api.expense;

import br.com.finances.api.generic.GenericDTO;

public class ExpenseDTO extends GenericDTO {
	
	private Category category;
	
	public ExpenseDTO(Expense model) {
		super(model);
		this.category = model.getCategory();
	}
	
	public Category getCategory() {
		if (category == null) {
			category = Category.Others;
		}
		return category;
	}
}
