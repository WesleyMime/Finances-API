import { NgClass, NgStyle } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { HeaderComponent } from "../header/header.component";
import { ReportsService } from './reports.service';
import { Expense, SummaryLastYear } from './summary-last-year';
import { SummaryByDate } from './summary-by-date';
import { firstValueFrom } from 'rxjs';
import { categoriesEnum } from '../category';
import { AiService } from './ai.service';
import { AiMessage } from './ai-message';

@Component({
  selector: 'app-reports',
  imports: [NgClass, NgStyle, HeaderComponent],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  loading = 'Carregando análise por IA...';
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
  monthComparisonTakeaway = this.loading;

  months = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];

  // Data for Income vs. Expenses
  incomeExpenseTotal = '';
  incomeExpensePercentage = '';
  lastMonth = '';
  monthOrderIncomeExpenseComparison: string[] = [];
  incomeExpenseBarHeights = [{ height: '', value: '' }];
  currentYear = 0;
  financialBalanceTakeaway = this.loading;
  // Data for Spending by Category
  spendingCategoriesMonth = categoriesEnum.map(category => ({ ...category, value: 0, valueCurrency: '', percentage: '0%' }));
  spendingByCategoriesMonthTakeaway = '';
  spendingCategories = categoriesEnum.map(category => ({ ...category, value: 0, valueCurrency: '', percentage: '0%' }));
  spendingByCategoriesYearTakeaway = this.loading;

  savingsRate = '';
  savingsTakeaway = this.loading;
  reportsService = inject(ReportsService);
  aiService = inject(AiService);

  async ngOnInit(): Promise<void> {
    let currentDate = new Date();
    let lastMonthDate = new Date(currentDate);
    lastMonthDate.setMonth(lastMonthDate.getMonth() - 1);

    const lastYearSummary = await this.getSummaryLastYear(lastMonthDate);
    const currentMonthSummary = await this.getSummaryByDate(currentDate);
    const lastMonthSummary = await this.getSummaryByDate(lastMonthDate);

    this.updateNetWorth(
      lastYearSummary.totalYearIncome + currentMonthSummary.totalIncome,
      lastYearSummary.totalYearExpense + currentMonthSummary.totalExpense);

    this.getMonthOverMonthComparison(currentDate, currentMonthSummary, lastMonthSummary);

    this.updateFinancialBalance(currentDate, lastYearSummary);

    this.updateSpendingByCategoryLastMonth(lastYearSummary, lastMonthSummary, currentDate);

    this.updateSpendingByCategoryYear(lastYearSummary.expenses);

    this.savingsRate = lastYearSummary.percentageSavingsRate;

    this.aiService.getSavingsTakeaway(this.savingsRate).subscribe({
      next: (response: AiMessage) => {
        this.savingsTakeaway = response.message;
      }
    })
  }

  private updateNetWorth(incomeValue: number, expenseValue: number): void {
    this.totalNetWorthValue += incomeValue - expenseValue;
    this.totalNetWorth = this.formatCurrency(this.totalNetWorthValue);

    this.totalAssetsValue += incomeValue;
    this.totalAssets = this.formatCurrency(this.totalAssetsValue);

    this.totalLiabilitiesValue += expenseValue;
    this.totalLiabilities = this.formatCurrency(this.totalLiabilitiesValue);
  }

  private getMonthOverMonthComparison(currentDate: Date, currentMonthSummary: SummaryByDate, lastMonthSummary: SummaryByDate): void {
    this.getMonthOrderForIncomeExpenseComparison(currentDate);

    let incomeCurrentMonth = currentMonthSummary.totalIncome;
    let expenseCurrentMonth = currentMonthSummary.totalExpense;

    this.incomeCurrentMonth = this.formatCurrency(incomeCurrentMonth);
    this.expenseCurrentMonth = this.formatCurrency(expenseCurrentMonth);

    let incomeLastMonth = lastMonthSummary.totalIncome;
    let expenseLastMonth = lastMonthSummary.totalExpense;

    let incomeValueDifference = incomeCurrentMonth - incomeLastMonth;
    let expenseValueDifference = (expenseLastMonth - expenseCurrentMonth) * -1; // To not show a negative number

    if (incomeValueDifference != 0 && incomeLastMonth != 0)
      this.incomeValueDifference = this.formatCurrency(incomeValueDifference);

    if (expenseValueDifference != 0 && expenseLastMonth != 0)
      this.expenseValueDifference = this.formatCurrency(expenseValueDifference);

    this.aiService.getMonthOverMonthComparisonTakeaway(incomeValueDifference, expenseValueDifference).subscribe({
      next: (response: AiMessage) => {
        this.monthComparisonTakeaway = response.message;
      }
    })

    this.percentualChangeFromLastMonth(
      incomeCurrentMonth,
      incomeLastMonth,
      expenseCurrentMonth,
      expenseLastMonth);
  }

  private updateFinancialBalance(currentDate: Date, summary: SummaryLastYear): void {
    this.currentYear = currentDate.getFullYear();
    this.incomeExpenseTotal = this.formatCurrency(summary.avgBalanceYear);
    this.getIncomeExpenseBarHeights(summary.finalBalanceEachMonth);

    this.aiService.getFinancialBalanceTakeaway(summary.finalBalanceEachMonth.toString()).subscribe({
      next: (response: AiMessage) => {
        this.financialBalanceTakeaway = response.message;
      }
    })
  }

  private updateSpendingByCategoryLastMonth(lastYearSummary: SummaryLastYear, lastMonthSummary: SummaryByDate, currentDate: Date): void {
    this.lastMonth = this.months[currentDate.getMonth() - 1];

    let diffBalancePercent = this.getPercentageChange(
      lastYearSummary.finalBalanceEachMonth[11], lastYearSummary.finalBalanceEachMonth[10]) * -1; // Invert the sign
    this.incomeExpensePercentage = this.formatPercentage(diffBalancePercent);

    lastMonthSummary.totalExpenseByCategory.forEach((item) => {
      this.spendingCategoriesMonth.forEach((category) => {
        if (item.category === category.name) category.value += item.totalValue;
      })
    });
    this.getValuesForLineChart(this.spendingCategoriesMonth);

    this.aiService.getSpendingByCategoryLastMonthTakeaway(this.spendingCategoriesMonth.map(category => ({
      name: category.namePtBr,
      value: category.value
    }))).subscribe({
      next: (response: AiMessage) => {
        this.spendingByCategoriesMonthTakeaway = response.message;
      }
    });
  }

  private updateSpendingByCategoryYear(list: Expense[]): void {
    list.forEach((expense) => {
      this.spendingCategories.forEach((category) => {
        if (expense.category === category.name) category.value += expense.value;
      })
    });
    this.getValuesForLineChart(this.spendingCategories);

    this.aiService.getSpendingByCategoryYearTakeaway(this.spendingCategories.map(category => ({
      name: category.namePtBr,
      value: category.value
    }))).subscribe({
      next: (response: AiMessage) => {
        this.spendingByCategoriesYearTakeaway = response.message;
      }
    });;
  }

  private getMonthOrderForIncomeExpenseComparison(currentDate: Date) {
    let month = currentDate.getMonth();
    let result = [];

    let j = 0;
    for (let i: number = month; j < 12; i++, j++) {
      result.push(this.months[i % 12]);
    }
    this.monthOrderIncomeExpenseComparison = result;
  }

  private percentualChangeFromLastMonth(incomeCurrentMonthValue: number, incomeLastMonthValue: number,
    expenseCurrentMonthValue: number, expenseLastMonthValue: number) {
    if (isNaN(incomeCurrentMonthValue) || isNaN(incomeLastMonthValue) ||
      isNaN(expenseCurrentMonthValue) || isNaN(expenseLastMonthValue)) return;

    let incomeChangePercentageValue = this.getPercentageChange(incomeCurrentMonthValue, incomeLastMonthValue);
    let expenseChangePercentageValue = this.getPercentageChange(expenseCurrentMonthValue, expenseLastMonthValue);
    this.incomeChangePercentage = this.formatPercentage(incomeChangePercentageValue);
    this.expenseChangePercentage = this.formatPercentage(expenseChangePercentageValue);
  }

  // Get the biggest value and consider it 100%, and then calculate the percentage for each month
  private getIncomeExpenseBarHeights(list: any): void {
    this.incomeExpenseBarHeights = [];
    let maxBalance = Math.max(...list);
    list.map((balance: number) => {
      let balanceCurrency = this.formatCurrency(balance);
      // Calculate the percentage height based on the maximum balances
      let percentage = (100 + this.getPercentageChange(balance, maxBalance)) + '%';
      this.incomeExpenseBarHeights.push({ height: percentage, value: balanceCurrency });
    });
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

  private async getSummaryLastYear(date: Date): Promise<SummaryLastYear> {
    const summary = await firstValueFrom(this.reportsService.getSummaryLastYear(date));
    console.log('Summary data for', date, summary);
    return summary;
  }

  private async getSummaryByDate(date: Date): Promise<SummaryByDate> {
    const summary = await firstValueFrom(this.reportsService.getSummaryByDate(date));
    console.log('Summary data for', date, summary);
    return summary;
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
}
