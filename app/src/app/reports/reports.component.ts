// src/app/reports/reports.component.ts
import { NgClass, NgStyle } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { HeaderComponent } from "../header/header.component";
import { ReportsService } from './reports.service';
import { Expense, SummaryLastYear } from './summary-last-year';
import { SummaryByDate } from './summary-by-date';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-reports',
  imports: [NgClass, NgStyle, HeaderComponent],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {

  // Data for Month-over-Month Comparison
  incomeCurrentMonth = '';
  expenseCurrentMonth = '';
  incomeValueDifference = '';
  expenseValueDifference = '';
  incomeChangePercentage = '';
  expenseChangePercentage = '';

  // Data for Income vs. Expenses
  incomeExpenseTotal = '';
  incomeExpensePercentage = '';
  incomeExpenseMonths = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];

  incomeExpenseBarHeights = [];

  // Data for Spending by Category
  spendingTotal = '';
  spendingChange = '-2%';
  categoriesBarWidth = [];
  spendingCategories = [
    { name: 'Food', namePtBr: 'Alimentação', value: 0, valueCurrency: '', percentage: '0%'},
    { name: 'Health', namePtBr: 'Saúde', value: 0, valueCurrency: '',  percentage: '0%'},
    { name: 'Transport', namePtBr: 'Transporte', value: 0, valueCurrency: '', percentage: '0%'},
    { name: 'Education', namePtBr: 'Educação', value: 0, valueCurrency: '',  percentage: '0%'},
    { name: 'Leisure', namePtBr: 'Lazer', value: 0, valueCurrency: '',  percentage: '0%'},
    { name: 'Unforeseen', namePtBr: 'Imprevísto', value: 0, valueCurrency: '',  percentage: '0%'},
    { name: 'Others', namePtBr: 'Outros', value: 0, valueCurrency: '',  percentage: '0%'},
  ];
  spendingCategoriesMonth = [
    { name: 'Food', namePtBr: 'Alimentação', value: 0, valueCurrency: '', percentage: '0%'},
    { name: 'Health', namePtBr: 'Saúde', value: 0, valueCurrency: '',  percentage: '0%'},
    { name: 'Transport', namePtBr: 'Transporte', value: 0, valueCurrency: '', percentage: '0%'},
    { name: 'Education', namePtBr: 'Educação', value: 0, valueCurrency: '',  percentage: '0%'},
    { name: 'Leisure', namePtBr: 'Lazer', value: 0, valueCurrency: '',  percentage: '0%'},
    { name: 'Unforeseen', namePtBr: 'Imprevísto', value: 0, valueCurrency: '',  percentage: '0%'},
    { name: 'Others', namePtBr: 'Outros', value: 0, valueCurrency: '',  percentage: '0%'},
  ];

  // Data for Net Worth Trend
  totalNetWorth = '';
  totalAssets = '';
  totalLiabilities = '';
  savingsRate = '';

  reportsService = inject(ReportsService);
  constructor() { }

  ngOnInit(): void {
    this.getMonthOverMonthComparison();
    this.reportsService.getSummaryLastYear(new Date()).subscribe({
      // Treat the response as a Report type
      next: (summary: SummaryLastYear) => {
        console.log('Summary data:', summary);

        this.incomeExpenseTotal = this.formatCurrency(summary.avgBalanceYear);
        this.getIncomeExpenseBarHeights(summary.finalBalanceEachMonth);

        let diffBalancePercent = this.getPercentageChange(
          summary.finalBalanceEachMonth[11], summary.finalBalanceEachMonth[10]) * -1; // Invert the sign
        this.incomeExpensePercentage = this.formatPercentage(diffBalancePercent);

        this.getSpendingByCategoryYear(summary.expenses);

        const totalYearIncome = summary.totalYearIncome;
        const totalYearExpense = summary.totalYearExpense;
        this.totalNetWorth = this.formatCurrency(totalYearIncome - totalYearExpense);
        this.totalAssets = this.formatCurrency(totalYearIncome);
        this.totalLiabilities = this.formatCurrency(totalYearExpense);
        this.savingsRate = summary.percentageSavingsRate;
    },
    error: (this.handleError)
    });
  }

  private async getMonthOverMonthComparison() {
    let incomeCurrentMonthValue: number = NaN;
    let expenseCurrentMonthValue: number = NaN;
    let incomeLastMonthValue: number = NaN;
    let expenselastMonthValue: number = NaN;

    let result = firstValueFrom(this.reportsService.getSummaryByDate(new Date()));
    await result.then((summary: SummaryByDate) => {
      console.log('Summary data:', new Date(), summary);
      incomeCurrentMonthValue = summary.totalIncome;
      expenseCurrentMonthValue = summary.totalExpense;
      this.incomeCurrentMonth = this.formatCurrency(incomeCurrentMonthValue);
      this.expenseCurrentMonth = this.formatCurrency(expenseCurrentMonthValue);

      this.getSpendingByCategoryMonth(summary);
    });

    let date = new Date();
    date.setMonth(date.getMonth() - 1);
    result = firstValueFrom(this.reportsService.getSummaryByDate(date));
    await result.then((summary: SummaryByDate) => {
      console.log('Summary data: ', date , summary);
      incomeLastMonthValue = summary.totalIncome;
      expenselastMonthValue = summary.totalExpense;
      this.incomeValueDifference = this.formatCurrency(incomeCurrentMonthValue- incomeLastMonthValue)
      this.expenseValueDifference = this.formatCurrency(expenselastMonthValue - expenseCurrentMonthValue);
      this.percentualChangeFromLastMonth(
        incomeCurrentMonthValue, 
        incomeLastMonthValue, 
        expenseCurrentMonthValue, 
        expenselastMonthValue);
    });
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
    let maxBalance = Math.max(...list);
    this.incomeExpenseBarHeights = list.map((balance: number) => {
      // Calculate the percentage height based on the maximum balance
      return (100 + this.getPercentageChange(balance, maxBalance)) + '%';
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
    if (isNaN(diffPercent)) return '0%';
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
