import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../header/header.component";
import { DatePipe } from '@angular/common';

// Interface for transaction data (optional but good practice)
interface Transaction {
  date: string;
  type: 'Income' | 'Expense';
  category: string;
  amount: number;
  notes: string;
  description?: string; // Add a description field for searching
}

@Component({
  selector: 'app-search-transactions',
  imports: [FormsModule, HeaderComponent, DatePipe],
  templateUrl: './search-transactions.component.html',
  styleUrls: ['./search-transactions.component.css']
})
export class SearchTransactionsComponent implements OnInit {

  date: string = '';
  description: string = '';
  selectedType: 'Both' | 'Income' | 'Expense' = 'Both';

  // Mock data - add a description field
  allTransactions: Transaction[] = [
    { date: '2024-03-15', type: 'Expense', category: 'Groceries', amount: -120.50, notes: 'Weekly grocery shopping', description: 'Shopping at local market' },
    { date: '2024-03-16', type: 'Income', category: 'Salary', amount: 3500.00, notes: 'Monthly paycheck', description: 'Company monthly salary deposit' },
    { date: '2024-03-17', type: 'Expense', category: 'Dining', amount: -45.75, notes: 'Dinner with friends', description: 'Dinner at Italian restaurant' },
    { date: '2024-03-18', type: 'Expense', category: 'Utilities', amount: -200.00, notes: 'Electricity bill', description: 'Monthly electricity payment' },
    { date: '2024-03-19', type: 'Income', category: 'Freelance', amount: 500.00, notes: 'Freelance project payment', description: 'Web design project payment' },
    { date: '2024-02-20', type: 'Expense', category: 'Transport', amount: -15.00, notes: 'Bus fare', description: 'Daily commute expense' },
     { date: '2024-01-20', type: 'Expense', category: 'Dining', amount: -30.00, notes: 'Lunch', description: 'Lunch meeting downtown' },
    { date: '2024-03-21', type: 'Income', category: 'Interest', amount: 5.50, notes: 'Savings account interest', description: 'Monthly interest from savings' },
  ];

  searchResults: Transaction[] = [];

  constructor() { }

  ngOnInit(): void {
    // Optionally display all transactions initially or load them here
    this.searchResults = [...this.allTransactions]; // Display all initially
  }

  onSearch(): void {
    let filteredResults = [...this.allTransactions]; // Start with all transactions

    // Filter by Start Date if provided
    if (this.date) {
      // Simple date string comparison for demonstration.
      // For robust date filtering, convert to Date objects and compare timestamps.
      filteredResults = filteredResults.filter(t => t.date >= this.date);
    }

    // Filter by Description if provided (case-insensitive substring match)
    if (this.description) {
      const lowerDescription = this.description.toLowerCase();
      filteredResults = filteredResults.filter(t =>
        t.description && t.description.toLowerCase().includes(lowerDescription)
      );
    }
    if (this.selectedType !== 'Both') {
      filteredResults = filteredResults.filter(t => t.type === this.selectedType);
    }

    this.searchResults = filteredResults;
  }
}