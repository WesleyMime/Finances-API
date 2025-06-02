// src/app/reports/reports.component.ts
import { NgClass, NgStyle } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { HeaderComponent } from "../header/header.component";
import { ReportsService } from './reports.service';
import { SummaryLastYear } from './summary-last-year';

@Component({
  selector: 'app-reports',
  imports: [NgClass, NgStyle, HeaderComponent],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {

  // Data for Month-over-Month Comparison
  incomeValue = '';
  expenseValue = '';
  incomeValueDifference = '';
  expenseValueDifference = '';
  incomeChangePercentage = '';
  expenseChangePercentage = '';

  // Data for Income vs. Expenses
  incomeExpenseTotal = '';
  incomeExpensePercentage = '';
  incomeExpenseMonths = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

  incomeExpenseBarHeights = [];
  incomeExpenseTakeaway = 'Key Takeaway: Your income has consistently exceeded your expenses over the past year, resulting in a positive cash flow. However, there was a slight dip in income during the summer months.';

  // Data for Spending by Category
  spendingTotal = '';
  spendingChange = '-2%';
  spendingCategories = [
    { name: 'Rent', amount: 2500, percentage: '32%', width: '100%' },
    { name: 'Food', amount: 1500, percentage: '19%', width: '60%' },
    { name: 'Entertainment', amount: 1000, percentage: '13%', width: '45%' },
    { name: 'Transportation', amount: 800, percentage: '10%', width: '38%' },
    { name: 'Utilities', amount: 1000, percentage: '13%', width: '45%' },
    { name: 'Shopping', amount: 1000, percentage: '13%', width: '45%' },
  ];
  spendingTakeaway = 'Key Takeaway: The largest portion of your spending is allocated to rent and food, followed by entertainment and transportation. Consider reviewing your spending in these categories to identify potential savings.';

  // Data for Net Worth Trend
  netWorthTotal = '$55,000';
  netWorthChange = '+15%';
  netWorthYears = ['2020', '2021', '2022', '2023'];
  netWorthTakeaway = 'Key Takeaway: Your net worth has shown a steady increase over the past four years, indicating strong financial growth. The most significant jump occurred in 2023, suggesting successful investments or savings strategies.';
  // For the line graph simulation, we'll use SVG directly in the template

  // Data for Savings Rate
  savingsRate = '25%';
  savingsRateChange = '+3%';
  savingsRateTakeaway = 'Key Takeaway: Your current savings rate is 25%, which is about the recommended average. This indicates a strong ability to save a significant portion of your income.';

  reportsService = inject(ReportsService);
  constructor() { }

  ngOnInit(): void {
    this.reportsService.getSummary(new Date()).subscribe({
      // Treat the response as a Report type
      next: (summary: SummaryLastYear) => {
        console.log('Summary data:', summary);
        this.getMonthOverMonthComparison(summary);

        this.incomeExpenseTotal = this.formatCurrency(summary.avgBalanceYear);
        this.getIncomeExpenseBarHeights(summary.finalBalanceEachMonth);

        let diffBalancePercent = this.getPercentageChange(
          summary.finalBalanceEachMonth[11], summary.finalBalanceEachMonth[10]);
        this.incomeExpensePercentage = this.formatPercentage(diffBalancePercent);

        this.netWorthTotal = this.formatCurrency(summary.avgBalanceYear);
        this.savingsRate = summary.percentageSavingsRate;
    },
    error: (error: Error) => {
      alert('Um erro aconteceu ao buscar o relatório. Por favor, tente novamente mais tarde.');
      console.error('Error fetching summary:', error)
    }
    });
  }

  private getMonthOverMonthComparison(summary: SummaryLastYear) {
    const incomeCurrentMonth = summary.income[11].value;
    const expensesCurrentMonth = summary.expenses[11].value;
    const incomeLastMonth = summary.income[10].value;
    const expensesLastMonth = summary.expenses[10].value;

    this.incomeValue = this.formatCurrency(incomeCurrentMonth);
    this.expenseValue = this.formatCurrency(expensesCurrentMonth);
    this.incomeValueDifference = this.formatCurrency(incomeCurrentMonth - incomeLastMonth);
    this.expenseValueDifference = this.formatCurrency(expensesCurrentMonth - expensesLastMonth);

    // Percentual change from last month
    this.incomeChangePercentage = this.formatPercentage(
      this.getPercentageChange(incomeCurrentMonth, incomeLastMonth));
    this.expenseChangePercentage = this.formatPercentage(
      this.getPercentageChange(expensesCurrentMonth, expensesLastMonth));
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

  // Helper to format currency
  formatCurrency(value: number): string {
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  // Helper to format percentage change
  getPercentageChange(num1: number, num2: number): number {
    let difference = num1 - num2;
    return difference * 100 / num2;
  }

  formatPercentage(diffPercent: number): string {
    return diffPercent > 0 ? `+${diffPercent}%` : `${diffPercent}%`;
  }
}
