package br.com.finances.api.expense;

import br.com.finances.api.generic.GenericDTO;

import java.io.Serializable;

public class ExpenseDTO extends GenericDTO implements Serializable {
	
	private Category category;
	
	public ExpenseDTO(Expense model) {
		super(model);
		this.category = model.getCategory();
	}
	
	public Category getCategory() {
		if (category == null) {
			category = Category.OTHERS;
		}
		return category;
	}
}
