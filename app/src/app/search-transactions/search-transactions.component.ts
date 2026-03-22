import { AfterViewInit, Component, ElementRef, inject, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../header/header.component";
import { CurrencyPipe, DatePipe, NgClass } from '@angular/common';
import { Transaction } from '../add-transaction/transaction';
import { categoriesEnum, getCategoryByNameInEnglish } from '../category';
import { SearchService } from './search.service';
import { RemoveTransactionComponent } from "./remove-transaction/remove-transaction.component";
import { TransactionService } from '../add-transaction/transaction.service';
import { Router, RouterLink } from '@angular/router';
import { Scroll } from './scroll';

@Component({
  selector: 'app-search-transactions',
  imports: [FormsModule, HeaderComponent, DatePipe, CurrencyPipe, RemoveTransactionComponent, RouterLink, NgClass],
  templateUrl: './search-transactions.component.html',
  styleUrls: ['./search-transactions.component.css']
})
export class SearchTransactionsComponent implements AfterViewInit{
  @ViewChild('totalSummaryContainer') totalSummaryContainer!: ElementRef;

  date: string | null = null;
  description: string | null = null;
  selectedType: 'Both' | 'Income' | 'Expense' = 'Both';

  selectedTransactions: Transaction[] = [];
  totalIncomeSelected: number = 0;
  totalExpenseSelected: number = 0;
  totalValueSelected: number = 0;
  isAllSelected: boolean = false;

  categories = categoriesEnum;

  router = inject(Router);
  searchService = inject(SearchService);
  transactionService = inject(TransactionService);

  transactionPendingRemoval: Transaction | null = null;
  searching = false;
  searchResults: Transaction[] = [];

  hasNextIncome: boolean = false;
  hasNextExpense: boolean = false;
  lastDateIncome: string | null = null;
  lastDateExpense: string | null = null;
  lastIdIncome: number | null = null;
  lastIdExpense: number | null = null;

  onSearch(firstSearch: boolean): void {
    this.searching = true;
    this.isAllSelected = false;
    if (firstSearch) {
      this.clearSearch();
    }

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
    if (this.description || !this.date) {
      if (this.incomeIsSelected() && (firstSearch || this.hasNextIncome)) {
        this.searchIncomeByDescription(this.description);
      }

      if (this.expenseIsSelected() && (firstSearch || this.hasNextExpense)) {
        this.searchExpenseByDescription(this.description);
      }
    }
    this.transactionPendingRemoval = null;
  }

  private searchIncomeByDescription(description: string | null) {
    this.searchService.searchIncomeByDescription(description, this.lastIdIncome, this.lastDateIncome)
      .subscribe({
        next: (result: Scroll) => {
          result.data.forEach((transaction) => {
            transaction.type = "Receita";
          });
          this.searchResults.push(...result.data);
          this.sort();
          this.lastIdIncome = result.lastId;
          this.lastDateIncome = result.lastDate;
          this.hasNextIncome = result.hasNext;
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

  private searchExpenseByDescription(description: string | null) {
    this.searchService.searchExpenseByDescription(description, this.lastIdExpense, this.lastDateExpense)
      .subscribe({
        next: (result: Scroll) => {
          result.data.forEach((transaction) => {
            transaction.type = "Despesa";
            transaction.category = getCategoryByNameInEnglish(transaction.category.toString());
            transaction.value = transaction.value * -1;
          });
          this.searchResults.push(...result.data);
          this.sort();
          this.lastIdExpense = result.lastId;
          this.lastDateExpense = result.lastDate;
          this.hasNextExpense = result.hasNext;
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
            transaction.category = getCategoryByNameInEnglish(transaction.category.toString());
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

  private clearSearch() {
    this.searchResults = [];
    this.lastDateIncome = null;
    this.lastDateExpense = null;
    this.lastIdIncome = null;
    this.lastIdExpense = null;
    this.hasNextIncome = false;
    this.hasNextExpense = false;
  }

  ngAfterViewInit(): void {
    this.addScrollListener();
  }

  addScrollListener() {
    window.addEventListener('scroll', () => {
      if (this.searchResults.length == 0 || !this.hasNextIncome && !this.hasNextExpense || this.searching)
        return;

      const scrollPosition = window.scrollY;
      const totalHeightOfPage = document.documentElement.scrollHeight;
      const thresholdHeight = totalHeightOfPage - (window.innerHeight * 1.1);
      if (scrollPosition >= thresholdHeight && !this.isSearching) {
        this.isSearching = true;
        this.onSearch(false);
        setTimeout(() => {
          this.isSearching = false;
        }, 1000);
      }
      if (this.selectedTransactions.length != 0) {
        const container = this.totalSummaryContainer.nativeElement;
        let containerSize = 350;
        if (scrollPosition > containerSize) {
          container.style.position = 'fixed';
        } else {
          container.style.position = 'sticky';
        }
      }
    });
  }

  toggleAll() {
    this.isAllSelected ? this.unselectAll() : this.selectAll();
  }

  selectAll() {
    this.searchResults.forEach(transaction => {
      if (!transaction.selected || transaction.selected == undefined) {
        this.sumTransaction(transaction);
      }
      transaction.selected = true;
    });
    this.isAllSelected = true;
  }

  unselectAll() {
    this.searchResults.forEach(transaction => {
      this.subtractTransaction(transaction);
      transaction.selected = false;
    });
    this.isAllSelected = false;
  }

  selectTransaction(transaction: Transaction): void {
    const isAlreadySelected = this.selectedTransactions.includes(transaction);
    if (isAlreadySelected) {
      this.subtractTransaction(transaction);
    } else {
      this.sumTransaction(transaction);
    }
  }

  private sumTransaction(transaction: Transaction): void {
    this.selectedTransactions.push(transaction);
    this.totalValueSelected += transaction.value;
    if (transaction.type === "Receita")
      this.totalIncomeSelected += transaction.value;
    if (transaction.type === "Despesa")
      this.totalExpenseSelected += transaction.value;
  }

  private subtractTransaction(transaction: Transaction): void {
    const index = this.selectedTransactions.indexOf(transaction);
    if (index == -1) return;

    this.selectedTransactions.splice(index, 1);
    this.totalValueSelected -= transaction.value;
    if (transaction.type === "Receita")
      this.totalIncomeSelected -= transaction.value;
    if (transaction.type === "Despesa")
      this.totalExpenseSelected -= transaction.value;
  }

  clear() {
    this.selectedTransactions = [];
    this.totalIncomeSelected = 0;
    this.totalExpenseSelected = 0;
    this.totalValueSelected = 0;
    this.unselectAll();
  }

  isSearching: boolean = false;
}
