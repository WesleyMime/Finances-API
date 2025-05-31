// src/app/reports/reports.component.ts
import { NgClass, NgFor, NgStyle } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { HeaderComponent } from "../header/header.component";
import { ReportsService } from './reports.service';

@Component({
  selector: 'app-reports',
  imports: [NgFor, NgClass, NgStyle, HeaderComponent],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {

  // Data for Income vs. Expenses
  incomeExpenseTotal = '';
  incomeExpenseChange = '-5%'; // Use string to handle the '%'
  incomeExpenseMonths = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'];
  // Simulate relative bar heights based on the image (adjust as needed)
  incomeExpenseBarHeights = ['60%', '70%', '50%', '40%', '30%', '65%', '80%']; // Example percentages
  incomeExpenseTakeaway = 'Key Takeaway: Your income has consistently exceeded your expenses over the past year, resulting in a positive cash flow. However, there was a slight dip in income during the summer months.';

  // Data for Spending by Category
  spendingTotal = '';
  spendingChange = '-2%';
  spendingCategories = [
    { name: 'Rent', amount: 2500, percentage: '32%', width: '100%' }, // Simulate width visually
    { name: 'Food', amount: 1500, percentage: '19%', width: '60%' },
    { name: 'Entertainment', amount: 1000, percentage: '13%', width: '45%' },
    { name: 'Transportation', amount: 800, percentage: '10%', width: '38%' },
    { name: 'Utilities', amount: 1000, percentage: '13%', width: '45%' },
    { name: 'Shopping', amount: 1000, percentage: '13%', width: '45%' },
  ]; // Add width for visual representation
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
      next: (summary: any) => {
        // Assuming summary contains the necessary data
        console.log('Summary data:', summary);
        this.incomeExpenseTotal = summary.avgBalanceYear.toLocaleString('en-US', { style: 'currency', currency: 'USD' });
        this.netWorthTotal = summary.avgBalanceYear.toLocaleString('en-US', { style: 'currency', currency: 'USD' });
        this.savingsRate = summary.percentageSavingsRate;

    },
    error: (error: Error) => {
      console.error('Error fetching summary:', error)
    }
    });
  }

  // Helper to determine text color for percentage change
  getChangeColor(change: string): string {
    if (change.startsWith('+')) {
      return 'green'; // Or a specific green class
    } else if (change.startsWith('-')) {
      return 'red'; // Or a specific red class
    }
    return 'gray'; // Default or neutral color
  }
}
