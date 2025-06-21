import { NgClass, NgStyle } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { HeaderComponent } from "../header/header.component";
import { ReportsService } from './reports.service';
import { Expense, SummaryLastYear } from './summary-last-year';
import { SummaryByDate } from './summary-by-date';
import { firstValueFrom } from 'rxjs';
import { categoriesEnum } from '../category';

@Component({
  selector: 'app-reports',
  imports: [NgClass, NgStyle, HeaderComponent],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {

  // Data for Net Worth Trend
  totalNetWorth = '';
  totalAssets = '';
  totalLiabilities = '';
  totalNetWorthValue = 0;
  totalAssetsValue = 0;
  totalLiabilitiesValue = 0;

  // Data for Month-over-Month Comparison
  incomeCurrentMonth = '';
  expenseCurrentMonth = '';
  incomeValueDifference = '';
  expenseValueDifference = '';
  incomeChangePercentage = '';
  expenseChangePercentage = '';

  months = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];

  // Data for Income vs. Expenses
  incomeExpenseTotal = '';
  incomeExpensePercentage = '';
  currentMonth = '';
  monthOrderincomeExpenseComparison: string[] = [];
  incomeExpenseBarHeights = [{height: '', value: ''}];
  currentYear = 0;

  // Data for Spending by Category
  spendingTotal = '';
  categoriesBarWidth = [];
  spendingCategories = categoriesEnum.map(category => ({ ...category, value: 0, valueCurrency: '', percentage: '0%' }));
  spendingCategoriesMonth = categoriesEnum.map(category => ({ ...category, value: 0, valueCurrency: '', percentage: '0%' }));

  savingsRate = '';

  reportsService = inject(ReportsService);
  constructor() { }

  ngOnInit(): void {
    var currentDate = new Date();
    this.getMonthOrderForIncomeExpenseComparison(currentDate);
    
    this.getMonthOverMonthComparison(currentDate);
    this.reportsService.getSummaryLastYear(currentDate).subscribe({
      // Treat the response as a Report type
      next: (summary: SummaryLastYear) => {
        console.log('Summary data:', summary);

        this.currentYear = currentDate.getFullYear();
        this.incomeExpenseTotal = this.formatCurrency(summary.avgBalanceYear);
        this.getIncomeExpenseBarHeights(summary.finalBalanceEachMonth);

        let diffBalancePercent = this.getPercentageChange(
          summary.finalBalanceEachMonth[11], summary.finalBalanceEachMonth[10]) * -1; // Invert the sign
        this.incomeExpensePercentage = this.formatPercentage(diffBalancePercent);
        this.currentMonth = this.months[currentDate.getMonth()];

        this.getSpendingByCategoryYear(summary.expenses);
        
        this.updateNetWorth(summary.totalYearIncome, summary.totalYearExpense);
        this.savingsRate = summary.percentageSavingsRate;
    },
    error: (this.handleError)
    });
  }

  private getMonthOrderForIncomeExpenseComparison(currentDate: Date) {
    var month = currentDate.getMonth();
    var result = [];

    var j = 0;
    for (let i: number = month; j < 12; i++) {
      j++;
      result.push(this.months[i % 12]);
    }
    this.monthOrderincomeExpenseComparison = result;
  }

  private async getMonthOverMonthComparison(currentDate: Date) {
    let incomeCurrentMonthValue: number = NaN;
    let expenseCurrentMonthValue: number = NaN;
    let incomeLastMonthValue: number = NaN;
    let expenselastMonthValue: number = NaN;

    let result = firstValueFrom(this.reportsService.getSummaryByDate(currentDate));
    await result.then((summary: SummaryByDate) => {
      console.log('Summary data:', currentDate, summary);
      incomeCurrentMonthValue = summary.totalIncome;
      expenseCurrentMonthValue = summary.totalExpense;
      this.incomeCurrentMonth = this.formatCurrency(incomeCurrentMonthValue);
      this.expenseCurrentMonth = this.formatCurrency(expenseCurrentMonthValue);

      this.updateNetWorth(incomeCurrentMonthValue, expenseCurrentMonthValue);
    });
    
    let date = currentDate;
    date.setMonth(date.getMonth() - 1);
    
    result = firstValueFrom(this.reportsService.getSummaryByDate(date));
    await result.then((summary: SummaryByDate) => {
      this.getSpendingByCategoryMonth(summary);
      console.log('Summary data: ', date , summary);
      incomeLastMonthValue = summary.totalIncome;
      expenselastMonthValue = summary.totalExpense;
      let incomeValueDifference = incomeCurrentMonthValue - incomeLastMonthValue;
      if (incomeValueDifference != 0 && incomeLastMonthValue != 0)
        this.incomeValueDifference = this.formatCurrency(incomeValueDifference);

      let expenseValueDifference = (expenselastMonthValue - expenseCurrentMonthValue) * -1; // To not show a negative number
      if (expenseValueDifference != 0 && expenselastMonthValue != 0)
        this.expenseValueDifference = this.formatCurrency(expenseValueDifference);

      this.percentualChangeFromLastMonth(
        incomeCurrentMonthValue, 
        incomeLastMonthValue, 
        expenseCurrentMonthValue, 
        expenselastMonthValue);
    });
  }

  private updateNetWorth(incomeValue: number, expenseValue: number) {
    this.totalNetWorthValue += incomeValue - expenseValue;
    this.totalNetWorth = this.formatCurrency(this.totalNetWorthValue);

    this.totalAssetsValue += incomeValue;
    this.totalAssets = this.formatCurrency(this.totalAssetsValue);

    this.totalLiabilitiesValue += expenseValue;
    this.totalLiabilities = this.formatCurrency(this.totalLiabilitiesValue);
  }

  // Helper to determine text color for percentage change
  getChangeColorGood(change: string): string {
    if (change.startsWith('+')) {
      return 'green';
    } else if (change.startsWith('-')) {
      return 'red';
    }
    return 'gray';
  }
  getChangeColorBad(change: string): string {
    if (change.startsWith('+')) {
      return 'red';
    } else if (change.startsWith('-')) {
      return 'green';
    }
    return 'gray';
  }

  // Get the biggest value and consider it 100%, and then calculate the percentage for each month
  getIncomeExpenseBarHeights(list: any): void {
    this.incomeExpenseBarHeights = [];
    let maxBalance = Math.max(...list);
    list.map((balance: number) => {
      var balanceCurrency = this.formatCurrency(balance);
      // Calculate the percentage height based on the maximum balances
      var percentage = (100 + this.getPercentageChange(balance, maxBalance)) + '%';
      this.incomeExpenseBarHeights.push({height: percentage, value: balanceCurrency});
    });
  }

  getSpendingByCategoryMonth(response: SummaryByDate): void {
    response.totalExpenseByCategory.forEach((item) => {
      this.spendingCategoriesMonth.map((category) => {
        if (item.category === category.name) category.value += item.totalValue;
      })
    });
    this.getValuesForLineChart(this.spendingCategoriesMonth);
  }

  getSpendingByCategoryYear(list: Expense[]): void {
    list.forEach((expense) => {
      this.spendingCategories.map((category) => {
        if (expense.category === category.name) category.value += expense.value;
      })
    });
    this.getValuesForLineChart(this.spendingCategories);
  }

  private getValuesForLineChart(list: any[]) {
    let maxValue = 1; // To make the percentage math works when no value
    list.forEach((category) => {
      if (category.value > maxValue) maxValue = category.value;
    });
    list.forEach((category) => {
      category.valueCurrency = this.formatCurrency(category.value);
      category.percentage = (100 + this.getPercentageChange(category.value, maxValue)) + '%';
    });
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  getPercentageChange(num1: number, num2: number): number {
    let difference = num1 - num2;
    return difference * 100 / num2;
  }

  formatPercentage(diffPercent: number): string {
    if (!isFinite(diffPercent)) return '0%';
    // 2 decimal places for percentage
    diffPercent = Math.round(diffPercent * 100) / 100;
    return diffPercent > 0 ? `+${diffPercent}%` : `${diffPercent}%`;
  }

  private percentualChangeFromLastMonth(incomeCurrentMonthValue: number, incomeLastMonthValue: number, 
      expenseCurrentMonthValue: number, expenselastMonthValue: number) {
    if (isNaN(incomeCurrentMonthValue) || isNaN(incomeLastMonthValue) || 
        isNaN(expenseCurrentMonthValue) || isNaN(expenselastMonthValue)) return;

    let incomeChangePercentageValue = this.getPercentageChange(incomeCurrentMonthValue, incomeLastMonthValue);
    let expenseChangePercentageValue = this.getPercentageChange(expenseCurrentMonthValue, expenselastMonthValue);
    this.incomeChangePercentage = this.formatPercentage(incomeChangePercentageValue);
    this.expenseChangePercentage = this.formatPercentage(expenseChangePercentageValue);
  }

  private handleError(error: Error) {
    alert('Um erro aconteceu ao buscar o relatório. Por favor, tente novamente mais tarde.');
    console.error('Error fetching summary:', error)
  }
}
