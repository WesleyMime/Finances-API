import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../header/header.component";
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Transaction } from '../add-transaction/transaction';
import { Category } from '../category';
import { SearchService } from './search.service';

@Component({
  selector: 'app-search-transactions',
  imports: [FormsModule, HeaderComponent, DatePipe, CurrencyPipe],
  templateUrl: './search-transactions.component.html',
  styleUrls: ['./search-transactions.component.css']
})
export class SearchTransactionsComponent  {

  date: string | null = null;
  description: string | null = null;
  selectedType: 'Both' | 'Income' | 'Expense' = 'Both';

  categoryEnum: Category[] = [
    {name: 'Food', namePtBr: 'Alimentação'},
    {name: 'Health', namePtBr: 'Saúde'},
    {name: 'Home', namePtBr: 'Casa'},
    {name: 'Transport', namePtBr: 'Transporte'},
    {name: 'Education', namePtBr: 'Educação'},
    {name: 'Leisure', namePtBr: 'Lazer'},
    {name: 'Unforeseen', namePtBr: 'Imprevísto'},
    {name: 'Others', namePtBr: 'Outros'}
  ];

  searchResults: Transaction[] = [];
  searchService = inject(SearchService);

  constructor() { }

  onSearch(): void {
    this.searchResults = [];
    if (this.date) {
      if (this.selectedType == 'Both' || this.selectedType == 'Income') {
        var date = this.formatDate(this.date);
        this.searchService.searchIncomeByDate(date.getFullYear(), date.getMonth())
          .subscribe({
            next: (result: Transaction[]) => {
              result.forEach((transaction) => {
                transaction.type = "Receita";
              })
              var filteredResults = this.filterResults(result);
              this.searchResults.push(...filteredResults);
            }
          });
      }
      
      if (this.selectedType == 'Both' || this.selectedType == 'Expense') {
        var date = this.formatDate(this.date);
        this.searchService.searchExpenseByDate(date.getFullYear(), date.getMonth())
          .subscribe({
            next: (result: Transaction[]) => {
              result.forEach((transaction) => {
                transaction.type = "Despesa";
                var result = this.categoryEnum.find(category => category.name == transaction.category);
                transaction.category = result?.namePtBr || "Outros";
                transaction.value = transaction.value * -1;
              })
              var filteredResults = this.filterResults(result);
              this.searchResults.push(...filteredResults);
            }
          });
      }
      return;
    }
    if (this.description) {
      if (this.selectedType == 'Both' || this.selectedType == 'Income') {
        this.searchService.searchIncomeByDescription(this.description)
          .subscribe({
            next: (result: Transaction[]) => {
              result.forEach((transaction) => {
                transaction.type = "Receita";
              })
              this.searchResults.push(...result);
            }
          });
      }

      if (this.selectedType == 'Both' || this.selectedType == 'Expense') {
        this.searchService.searchExpenseByDescription(this.description)
          .subscribe({
            next: (result: Transaction[]) => {
              result.forEach((transaction) => {
                transaction.type = "Despesa";
                var result = this.categoryEnum.find(category => category.name == transaction.category);
                transaction.category = result?.namePtBr || "Outros";
                transaction.value = transaction.value * -1;
              })
              this.searchResults.push(...result);
            }
          });
      }
    }
  }

  private filterResults(transactions: Transaction[]): Transaction[] {
    debugger
    if (this.description) {
      var filteredResults = transactions.filter((transaction) => {
        return transaction.description.includes(this.description?this.description:'');
      });
      return filteredResults;
    }
    return transactions;
  }

  private formatDate(date: string): Date {
    var dateSplit = date.split("-");
    // new Date using html type month returns day 1 at 00:00, 
    // but because of GMT -3 it goes to day 31/30 21:00,
    // So i'm parsing the string eg 2025-01.
    return new Date(Number.parseFloat(dateSplit[0]), Number.parseFloat(dateSplit[1]), 1);
  }
}