package br.com.finances.api.income;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;

import br.com.finances.api.generic.GenericModel;

@Entity
public class Income extends GenericModel{
	
	public Income() {
		
	}
	
	public Income(String description, BigDecimal value, LocalDate date) {
		this.setDescription(description);
		this.setValue(value);
		this.setDate(date);
	}
}
