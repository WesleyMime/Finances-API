import { NgClass, NgStyle } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { HeaderComponent } from "../header/header.component";
import { SummaryService } from '../summary/summary.service';
import { Expense, SummaryLastYear } from '../summary/summary-last-year';
import { SummaryByMonth } from '../summary/summary-by-month';
import { forkJoin } from 'rxjs';
import { categoriesEnum } from '../category';
import { AiService } from './ai.service';
import { AiMessage } from './ai-message';
import { LoadingValueComponent } from '../loading-value/loading-value.component';
import { ToggleVisibilityService } from '../hide-value/toggle-visibility.service';
import { HideValueComponent } from '../hide-value/hide-value.component';
import { UtilsService } from '../utils/utils.service';
import { DateService } from '../utils/date.service';

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

  currentDate = new Date();

  // Data for Income vs. Expenses
  incomeExpenseTotal = '';
  monthOrderIncomeExpenseComparison: string[] = [];
  incomeExpenseBarHeights = [{ height: '', value: '' }];
  currentYear = 0;
  financialBalanceTakeaway = '';

  // Data for Spending by Category
  spendingCategoriesMonth = categoriesEnum.map(category => ({
    ...category,
    value: 0,
    valueBefore: 0,
    valueCurrency: '',
    valueBeforeCurrency: '',
    percentage: '0%',
    percentageBefore: '0%',
    percentageDifference: '0%'
  }));
  spendingByCategoriesMonthTakeaway = '';
  spendingCategories = categoriesEnum.map(category => ({ ...category, value: 0, valueBefore: 0, valueCurrency: '', percentage: '0%' }));
  spendingByCategoriesYearTakeaway = '';

  savingsRate = '';
  savingsTakeaway = '';

  summaryService = inject(SummaryService);
  aiService = inject(AiService);
  toggleService = inject(ToggleVisibilityService);
  utilsService = inject(UtilsService);
  dateService = inject(DateService);

  ngOnInit() {
    let lastMonthDate = this.dateService.removeMonths(this.currentDate, 1);
    let lastLastMonthDate = this.dateService.removeMonths(this.currentDate, 2);

    forkJoin({
      lastYearSummary: this.summaryService.getSummaryLastYear(),
      currentMonthSummary: this.summaryService.getSummaryByMonth(this.currentDate),
      lastMonthSummary: this.summaryService.getSummaryByMonth(lastMonthDate),
      lastLastMonthSummary: this.summaryService.getSummaryByMonth(lastLastMonthDate)
    }).subscribe({
      next: (s) => {
        this.updateNetWorth(s.lastYearSummary.totalYearIncome, s.lastYearSummary.totalYearExpense);
        this.updateFinancialBalance(this.currentDate, s.lastYearSummary);
        this.getMonthOverMonthComparison(s.currentMonthSummary, s.lastMonthSummary);
        this.updateSpendingByCategoryLastMonth(
          s.lastYearSummary, s.lastMonthSummary, s.lastLastMonthSummary);
        this.updateSpendingByCategoryYear(s.lastYearSummary.expenses);
        this.savingsRate = s.lastYearSummary.percentageSavingsRate;
        this.aiService.getSavingsTakeaway(this.savingsRate).subscribe({
          next: (response: AiMessage) => {
            this.savingsTakeaway = response.message;
          }
        })
      }
    });

  }

  private updateNetWorth(incomeValue: number, expenseValue: number): void {
    this.totalNetWorthValue += incomeValue - expenseValue;
    this.totalNetWorth = this.utilsService.formatCurrency(this.totalNetWorthValue);

    this.totalAssetsValue += incomeValue;
    this.totalAssets = this.utilsService.formatCurrency(this.totalAssetsValue);

    this.totalLiabilitiesValue += expenseValue;
    this.totalLiabilities = this.utilsService.formatCurrency(this.totalLiabilitiesValue);
  }

  private getMonthOverMonthComparison(currentMonthSummary: SummaryByMonth, lastMonthSummary: SummaryByMonth): void {
    this.getMonthOrderForIncomeExpenseComparison();

    let incomeCurrentMonth = currentMonthSummary.totalIncome;
    let expenseCurrentMonth = currentMonthSummary.totalExpense;

    this.incomeCurrentMonth = this.utilsService.formatCurrency(incomeCurrentMonth);
    this.expenseCurrentMonth = this.utilsService.formatCurrency(expenseCurrentMonth);

    let incomeLastMonth = lastMonthSummary.totalIncome;
    let expenseLastMonth = lastMonthSummary.totalExpense;

    this.incomeLastMonth = this.utilsService.formatCurrency(incomeLastMonth);
    this.expenseLastMonth = this.utilsService.formatCurrency(expenseLastMonth);

    let incomeValueDifference = incomeCurrentMonth - incomeLastMonth;
    let expenseValueDifference = (expenseLastMonth - expenseCurrentMonth) * -1; // To not show a negative number

    if (incomeValueDifference != 0 && incomeLastMonth != 0)
      this.incomeValueDifference = this.utilsService.formatCurrency(incomeValueDifference);

    if (expenseValueDifference != 0 && expenseLastMonth != 0)
      this.expenseValueDifference = this.utilsService.formatCurrency(expenseValueDifference);

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
    this.incomeExpenseTotal = this.utilsService.formatCurrency(summary.avgBalanceYear);
    this.getIncomeExpenseBarHeights(summary.finalBalanceEachMonth);

    this.aiService.getFinancialBalanceTakeaway(summary.finalBalanceEachMonth.toString()).subscribe({
      next: (response: AiMessage) => {
        this.financialBalanceTakeaway = response.message;
      }
    })
  }

  private updateSpendingByCategoryLastMonth(lastYearSummary: SummaryLastYear, lastMonthSummary: SummaryByMonth, lastLastMonthSummary: SummaryByMonth): void {
    this.expenseLastLastMonth = this.utilsService.formatCurrency(lastLastMonthSummary.totalExpense);

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

  private getMonthOrderForIncomeExpenseComparison() {
    let result = [];
    for (let i = 0; i < 12; i++) {
      result.push(this.dateService.getRelativeMonthName(i));
    }
    this.monthOrderIncomeExpenseComparison = result;
  }

  private percentualChangeFromLastMonth(incomeCurrentMonthValue: number, incomeLastMonthValue: number,
    expenseCurrentMonthValue: number, expenseLastMonthValue: number) {
    if (isNaN(incomeCurrentMonthValue) || isNaN(incomeLastMonthValue) ||
      isNaN(expenseCurrentMonthValue) || isNaN(expenseLastMonthValue)) return;

    this.incomeChangePercentage = this.utilsService.percentageChangeFormated(incomeCurrentMonthValue, incomeLastMonthValue);
    this.expenseChangePercentage = this.utilsService.percentageChangeFormated(expenseCurrentMonthValue, expenseLastMonthValue);
  }

  // Get the biggest value and consider it 100%, and then calculate the percentage for each month
  private getIncomeExpenseBarHeights(list: any): void {
    this.incomeExpenseBarHeights = [];
    let maxBalance = Math.max(...list);
    list.map((balance: number) => {
      let balanceCurrency = this.utilsService.formatCurrency(balance);
      // Calculate the percentage height based on the maximum balances
      let percentage = (100 + this.utilsService.getPercentageChange(balance, maxBalance)) + '%';
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
      category.valueCurrency = this.utilsService.formatCurrency(category.value);
      category.valueBeforeCurrency = this.utilsService.formatCurrency(category.valueBefore);
      category.percentage = (100 + this.utilsService.getPercentageChange(category.value, maxValue)) + '%';
      category.percentageBefore = (100 + this.utilsService.getPercentageChange(category.valueBefore, maxValue)) + '%';
      category.percentageDifference = this.utilsService.percentageChangeFormated(category.value, category.valueBefore);
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
    return this.utilsService.getChangeColorGood(change);
  }

  getChangeColorBad(change: string): string {
    return this.utilsService.getChangeColorBad(change);
  }

  getRelativeMonthName(n: number) : string {
    return this.dateService.getRelativeMonthName(n);
  }
}
