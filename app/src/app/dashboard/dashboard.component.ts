import { Component } from '@angular/core';
import { CurrencyPipe, NgClass, NgFor } from '@angular/common';
import { HeaderComponent } from "../header/header.component";

interface Transaction {
  date: string;
  category: string;
  description: string;
  amount: number;
}

@Component({
  selector: 'app-dashboard',
  imports: [CurrencyPipe, NgClass, NgFor, HeaderComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  // Mock Data (replace with actual data fetched from a service)
  netWorth: number = 125450;
  totalAssets: number = 150000;
  totalLiabilities: number = 24550;

  incomeExpenseChange: number = 2500;
  incomeExpensePercentage: number = 15;

  netWorthTrendChange: number = 10000;
  netWorthTrendPercentage: number = 10;

  // Mock transaction data
  recentTransactions: Transaction[] = [
    { date: '2024-07-15', category: 'Groceries', description: 'Supermarket purchase', amount: -85.50 },
    { date: '2024-07-14', category: 'Salary', description: 'Monthly paycheck', amount: 5000.00 },
    { date: '2024-07-12', category: 'Rent', description: 'Apartment rent', amount: -1500.00 },
    { date: '2024-07-10', category: 'Dining', description: 'Restaurant dinner', amount: -60.00 },
    { date: '2024-07-08', category: 'Utilities', description: 'Electricity bill', amount: -120.00 },
  ];

  getAmountClass(amount: number): string {
    if (amount > 0) {
      return 'amount-positive';
    } 
    if (amount < 0) {
      return 'amount-negative';
    }
    return '';
  }

  formatAmount(amount: number): string {
      const formatted = amount.toFixed(2);
      if (amount > 0) {
          return `+${formatted}`;
      }
      return formatted;
  }
}