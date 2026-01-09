import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../header/header.component";
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Transaction } from '../add-transaction/transaction';
import { categoriesEnum, getCategoryNameInPortuguese } from '../category';
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
export class SearchTransactionsComponent {

  date: string | null = null;
  description: string | null = null;
  selectedType: 'Both' | 'Income' | 'Expense' = 'Both';

  categories = categoriesEnum;

  searchResults: Transaction[] = [];
  searchService = inject(SearchService);
  transactionService = inject(TransactionService);

  transactionPendingRemoval: Transaction | null = null;
  searching = false;
  router: Router;
  constructor(router: Router) {
    this.router = router;
  }

  onSearch(): void {
    this.searching = true;
    this.searchResults = [];
    if (this.date) {
      let date = this.date.replace("-", "/");
      if (this.incomeIsSelected()) {
        this.searchIncomeByDate(date);
      }
      if (this.expenseIsSelected()) {
        this.searchExpenseByDate(date);
      }
      return;
    }
    if (!this.description && !this.date) {
      this.description = " ";
    }
    if (this.description) {
      if (this.incomeIsSelected()) {
        this.searchIncomeByDescription(this.description);
      }

      if (this.expenseIsSelected()) {
        this.searchExpenseByDescription(this.description);
      }
    }
    this.transactionPendingRemoval = null;
  }

  private searchIncomeByDescription(description: string) {
    this.searchService.searchIncomeByDescription(description)
      .subscribe({
        next: (result: Transaction[]) => {
          result.forEach((transaction) => {
            transaction.type = "Receita";
          });
          this.searchResults.push(...result);
          this.sort();
          this.searching = false;
        },
        error: (error) => {
          console.error('Error searching income:', error);
          this.searching = false;
        }
      });
  }

  private searchIncomeByDate(date: string) {
    this.searchService.searchIncomeByDate(date)
      .subscribe({
        next: (result: Transaction[]) => {
          result.forEach((transaction) => {
            transaction.type = "Receita";
          });
          let filteredResults = this.filterByDescription(result);
          this.searchResults.push(...filteredResults);
          this.sort();
          this.searching = false;
        },
        error: (error) => {
          console.error('Error searching income:', error);
          this.searching = false;
        }
      });
  }

  private searchExpenseByDescription(description: string) {
    this.searchService.searchExpenseByDescription(description)
      .subscribe({
        next: (result: Transaction[]) => {
          result.forEach((transaction) => {
            transaction.type = "Despesa";
            transaction.category = getCategoryNameInPortuguese(transaction.category);
            transaction.value = transaction.value * -1;
          });
          this.searchResults.push(...result);
          this.sort();
          this.searching = false;
        },
        error: (error) => {
          console.error('Error searching expense:', error);
          this.searching = false;
        }
      });
  }

  private searchExpenseByDate(date: string) {
    this.searchService.searchExpenseByDate(date)
      .subscribe({
        next: (result: Transaction[]) => {
          result.forEach((transaction) => {
            transaction.type = "Despesa";
            transaction.category = getCategoryNameInPortuguese(transaction.category);
            transaction.value = transaction.value * -1;
          });
          let filteredResults = this.filterByDescription(result);
          this.searchResults.push(...filteredResults);
          this.sort();
          this.searching = false;
        },
        error: (error) => {
          console.error('Error searching expense:', error);
          this.searching = false;
        }
      });
  }

  private filterByDescription(transactions: Transaction[]): Transaction[] {
    if (this.description) {
      this.description = this.description.trim();
      let filteredResults = transactions.filter((transaction) => {
        return transaction.description.toLocaleLowerCase()
          .includes(this.description ?? ''.toLocaleLowerCase());
      });
      return filteredResults;
    }
    return transactions;
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
    let id = transaction.id ?? Number.NaN;
    let type = transaction.type;
    if (type === "Receita") {
      const indexToRemove = this.searchResults.findIndex(t => t.id === this.transactionPendingRemoval?.id);
      if (indexToRemove > -1) {
        this.searchResults.splice(indexToRemove, 1);
      }
      this.transactionService.deleteIncome(id).subscribe({
        next: () => {
          console.log('Receita removida com sucesso');
        }
      });
    }
    if (type === "Despesa") {
      const indexToRemove = this.searchResults.findIndex(t => t.id === this.transactionPendingRemoval?.id);
      if (indexToRemove > -1) {
        this.searchResults.splice(indexToRemove, 1);
      }
      this.transactionService.deleteExpense(id).subscribe({
        next: () => {
          console.log('Despesa removida com sucesso');
        }
      });
    }
    this.transactionPendingRemoval = null;
  }

  incomeIsSelected() {
    return this.selectedType == 'Both' || this.selectedType == 'Income';
  }

  expenseIsSelected() {
    return this.selectedType == 'Both' || this.selectedType == 'Expense'
  }

  sort() {
    this.searchResults.sort((a: Transaction, b: Transaction) => {
      if (a.date < b.date)
        return 1;
      if (a.date == b.date)
        return 0;
      return -1;
    });
  }
}
