package br.com.finances.api.expense;

import br.com.finances.api.generic.GenericModel;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Expense extends GenericModel {

	private Category category;
	
	public Expense() {
		
	}
	
	public Expense(String description, BigDecimal value, LocalDate date, Category category) {
		super(description, value, date);
		this.category = category;
	}
	
	public Category getCategory() {
		if(category == null) {
			this.category = Category.Others;
		}
		return category;
	}	
	
	public void setCategory(Category category) {
		this.category = category;
	}
}
