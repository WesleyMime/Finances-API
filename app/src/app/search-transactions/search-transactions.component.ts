import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../header/header.component";
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Transaction } from '../add-transaction/transaction';
import { Category } from '../category';
import { SearchService } from './search.service';
import { RemoveTransactionComponent } from "./remove-transaction/remove-transaction.component";
import { TransactionService } from '../add-transaction/transaction.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-search-transactions',
  imports: [FormsModule, HeaderComponent, DatePipe, CurrencyPipe, RemoveTransactionComponent, RouterLink],
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
  
  searchResultsIncome: Transaction[] = [];
  searchResultsExpenses: Transaction[] = [];
  searchService = inject(SearchService);
  transactionService = inject(TransactionService);
  
  transactionPendingRemoval: Transaction | null = null;
  router: Router;
  constructor(router: Router) {
    this.router = router;
  }
  
  onSearch(): void {
    this.searchResultsIncome = [];
    this.searchResultsExpenses = [];
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
            this.searchResultsIncome.push(...filteredResults);
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
                var result = this.categoryEnum.filter(category => category.name == transaction.category);
                transaction.category = result[0].namePtBr;
                transaction.value = transaction.value * -1;
              })
              var filteredResults = this.filterResults(result);
              this.searchResultsExpenses.push(...filteredResults);
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
              this.searchResultsIncome.push(...result);
            }
          });
        }
        
        if (this.selectedType == 'Both' || this.selectedType == 'Expense') {
          this.searchService.searchExpenseByDescription(this.description)
          .subscribe({
            next: (result: Transaction[]) => {
              result.forEach((transaction) => {
                transaction.type = "Despesa";
                var result = this.categoryEnum.filter(category => category.name == transaction.category);
                transaction.category = result[0].namePtBr;
                transaction.value = transaction.value * -1;
              })
              this.searchResultsExpenses.push(...result);
            }
          });
        }
      }
      this.transactionPendingRemoval = null;
    }
    
    private filterResults(transactions: Transaction[]): Transaction[] {
      if (this.description) {
        var filteredResults = transactions.filter((transaction) => {
          return transaction.description.toLocaleLowerCase()
          .includes(this.description ? this.description : ''.toLocaleLowerCase());
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
    
  editTransaction(transaction: Transaction) {
    if (transaction.value < 0) {
      transaction.value = transaction.value * -1;
    }
    this.router.navigate(['/transactions/edit'], { state: { transaction: transaction } });
  }
  
  removeTransaction(transaction: Transaction): void {
    this.transactionPendingRemoval = transaction;
  }
  
  handleCancelRemoval(): void {
    this.transactionPendingRemoval = null;
  }
  
  handleConfirmRemoval(transaction: Transaction): void {
    var id = transaction.id? transaction.id : NaN;
    let type = transaction.type;
    if (type === "Receita") {
      const indexToRemove = this.searchResultsIncome.findIndex(t => t.id === this.transactionPendingRemoval?.id);
      if (indexToRemove > -1) {
        this.searchResultsIncome.splice(indexToRemove, 1);
      }
      this.transactionService.deleteIncome(id).subscribe({
        next: () => {
          console.log('Receita removida com sucesso');
        }
      });
    }
    if (type === "Despesa") {
      const indexToRemove = this.searchResultsExpenses.findIndex(t => t.id === this.transactionPendingRemoval?.id);
      if (indexToRemove > -1) {
        this.searchResultsExpenses.splice(indexToRemove, 1);
      }
      this.transactionService.deleteExpense(id).subscribe({
        next: () => {
          console.log('Despesa removida com sucesso');
        }
      });
    }
    this.transactionPendingRemoval = null;
  }
}