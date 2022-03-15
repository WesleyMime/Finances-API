package br.com.finances.api.summary;

import java.math.BigDecimal;
import java.util.List;

import br.com.finances.api.expense.ExpenseCategoryDTO;

public class SummaryDTO{
	
	private BigDecimal totalIncome;
	private BigDecimal totalExpense;	
	private BigDecimal finalBalance;
	private List<ExpenseCategoryDTO> totalExpenseByCategory;
	
	public SummaryDTO() {
	}

	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}
	
	public void setTotalExpense(BigDecimal totalExpense) {
		this.totalExpense = totalExpense;
	}
	
	public void setFinalBalance(BigDecimal finalBalance) {
		this.finalBalance = finalBalance;
	}
	
	public void setTotalExpenseByCategory(List<ExpenseCategoryDTO> totalExpenseByCategory) {
		this.totalExpenseByCategory = totalExpenseByCategory;
	}
	
	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	public BigDecimal getTotalExpense() {
		return totalExpense;
	}
		
	public BigDecimal getFinalBalance() {
		return finalBalance;
	}
	
	public List<ExpenseCategoryDTO> getTotalExpenseByCategory() {
		return totalExpenseByCategory;
	}
}
