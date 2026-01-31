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
import { LoadingValueComponent } from '../loading-value/loading-value.component';
import { ToggleVisibilityService } from '../hide-value/toggle-visibility.service';
import { HideValueComponent } from '../hide-value/hide-value.component';

@Component({
  selector: 'app-reports',
  imports: [NgClass, NgStyle, HeaderComponent, LoadingValueComponent, HideValueComponent],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  loading = 'Carregando análise por IA...';

  hiddenValue = '*****';
  hidden = false;
  opacity: number = 1;

  // Data for Net Worth Trend
  totalNetWorth = ''
  totalAssets = '';
  totalLiabilities = '';
  totalNetWorthValue = 0;
  totalAssetsValue = 0;
  totalLiabilitiesValue = 0;

  // Data for Month-over-Month Comparison
  incomeCurrentMonth = '';
  expenseCurrentMonth = '';
  incomeLastMonth = '';
  expenseLastMonth = '';
  expenseLastLastMonth = '';
  incomeValueDifference = '';
  expenseValueDifference = '';
  incomeChangePercentage = '';
  expenseChangePercentage = '';
  monthComparisonTakeaway = '';

  months = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
  currentDate = new Date();
  currentMonth = this.months[this.currentDate.getMonth() % 12];

  // Data for Income vs. Expenses
  incomeExpenseTotal = '';
  incomeExpensePercentage = '';
  lastMonth = '';
  lastLastMonth = '';
  monthOrderIncomeExpenseComparison: string[] = [];
  incomeExpenseBarHeights = [{ height: '', value: '' }];
  currentYear = 0;
  financialBalanceTakeaway = '';
  // Data for Spending by Category
  spendingCategoriesMonth = categoriesEnum.map(category => ({ ...category, value: 0, valueBefore: 0, valueCurrency: '', valueBeforeCurrency: '', percentage: '0%', percentageBefore: '0%' }));
  spendingByCategoriesMonthTakeaway = '';
  spendingCategories = categoriesEnum.map(category => ({ ...category, value: 0, valueBefore: 0, valueCurrency: '', percentage: '0%' }));
  spendingByCategoriesYearTakeaway = '';

  savingsRate = '';
  savingsTakeaway = '';
  reportsService = inject(ReportsService);
  aiService = inject(AiService);
  toggleService = inject(ToggleVisibilityService);
    let lastMonthDate = new Date(this.currentDate);
    lastMonthDate.setMonth(lastMonthDate.getMonth() - 1);

    let lastLastMonthDate = new Date(this.currentDate);
    lastLastMonthDate.setMonth(lastLastMonthDate.getMonth() - 2);

    const lastYearSummary = await this.getSummaryLastYear();
    const currentMonthSummary = await this.getSummaryByMonth(this.currentDate);
    const lastMonthSummary = await this.getSummaryByMonth(lastMonthDate);
    const lastLastMonthSummary = await this.getSummaryByMonth(lastLastMonthDate);

    this.updateNetWorth(
      lastYearSummary.totalYearIncome,
      lastYearSummary.totalYearExpense);

    this.getMonthOverMonthComparison(this.currentDate, currentMonthSummary, lastMonthSummary);

    this.updateFinancialBalance(this.currentDate, lastYearSummary);

    this.updateSpendingByCategoryLastMonth(lastYearSummary, lastMonthSummary, lastLastMonthSummary, this.currentDate);

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

    this.incomeLastMonth = this.formatCurrency(incomeLastMonth);
    this.expenseLastMonth = this.formatCurrency(expenseLastMonth);

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

  private updateSpendingByCategoryLastMonth(lastYearSummary: SummaryLastYear, lastMonthSummary: SummaryByDate, lastLastMonthSummary: SummaryByDate, currentDate: Date): void {
    let month = currentDate.getMonth() - 1;
    if (month < 0)
      month = 11;

    this.lastMonth = this.months[month];
    this.lastLastMonth = this.months[month - 1];

    this.expenseLastLastMonth = this.formatCurrency(lastLastMonthSummary.totalExpense);

    let diffBalancePercent = this.getPercentageChange(
      lastYearSummary.finalBalanceEachMonth[11], lastYearSummary.finalBalanceEachMonth[10]) * -1; // Invert the sign
    this.incomeExpensePercentage = this.formatPercentage(diffBalancePercent);

    lastMonthSummary.totalExpenseByCategory.forEach((item) => {
      this.spendingCategoriesMonth.forEach((category) => {
        if (item.category === category.name) category.value += item.totalValue;
      })
    });
    lastLastMonthSummary.totalExpenseByCategory.forEach((item) => {
      this.spendingCategoriesMonth.forEach((category) => {
        if (item.category === category.name) category.valueBefore += item.totalValue;
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
      if (category.valueBefore > maxValue) maxValue = category.valueBefore;
    });
    list.forEach((category) => {
      category.valueCurrency = this.formatCurrency(category.value);
      category.valueBeforeCurrency = this.formatCurrency(category.valueBefore);
      category.percentage = (100 + this.getPercentageChange(category.value, maxValue)) + '%';
      category.percentageBefore = (100 + this.getPercentageChange(category.valueBefore, maxValue)) + '%';
    });
  }

  toggleValues() {
    this.hidden = this.toggleService.isHidden;
    this.fadeIn();
  }

  fadeIn(): void {
    this.opacity = 0;
    const step = () => {
      this.opacity += 0.015;
      if (this.opacity < 1) {
        requestAnimationFrame(step);
      }
    };

    requestAnimationFrame(step);
  }

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
