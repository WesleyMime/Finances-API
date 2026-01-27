package br.com.finances.api.summary;

import java.io.Serializable;
import java.math.BigDecimal;

public class SummaryBasicDTO implements Serializable {
	private BigDecimal totalIncome = BigDecimal.ZERO;
	private BigDecimal totalExpense = BigDecimal.ZERO;
	private BigDecimal totalBalance = BigDecimal.ZERO;

	public SummaryBasicDTO() {
	}

	public SummaryBasicDTO(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal totalBalance) {
		this.totalIncome = totalIncome;
		this.totalExpense = totalExpense;
		this.totalBalance = totalBalance;
	}

	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	public void increaseTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = this.totalIncome.add(totalIncome);
	}

	public BigDecimal getTotalExpense() {
		return totalExpense;
	}

	public void increaseTotalExpense(BigDecimal totalExpense) {
		this.totalExpense = this.totalExpense.add(totalExpense);
	}

	public BigDecimal getTotalBalance() {
		return totalBalance;
	}

	public void increaseTotalBalance(BigDecimal balance) {
		this.totalBalance = this.totalBalance.add(balance);
	}

	public void decreaseTotalBalance(BigDecimal balance) {
		this.totalBalance = this.totalBalance.subtract(balance);
	}
}