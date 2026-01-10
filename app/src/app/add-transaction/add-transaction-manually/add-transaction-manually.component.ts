import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../../header/header.component";
import { TransactionService } from './../transaction.service';
import { emptyTransaction, Transaction } from '../transaction';
import { Router } from '@angular/router';
import { categoriesEnum, getCategoryNameInEnglish, getCategoryNameInPortuguese } from '../../category';
import { isExpense, isIncome, transactionTypeEnum } from '../transaction-types';
import { LoadingValueComponent } from '../../loading-value/loading-value.component';

@Component({
  selector: 'add-transaction-manually',
  imports: [FormsModule, HeaderComponent, LoadingValueComponent],
  templateUrl: './add-transaction-manually.component.html',
  styleUrls: ['./add-transaction-manually.component.css']
})
export class AddTransactionManuallyComponent implements OnInit {
  isEditMode = false;
  transaction: Transaction = JSON.parse(JSON.stringify(emptyTransaction)); // Copy without reference

  successMessage: string | null = null;
  errorMessage: string | null = null;
  isLoading: boolean = false;

  transactionTypes = transactionTypeEnum;

  transactionService = inject(TransactionService);
  router = inject(Router);
  categories = categoriesEnum;

  ngOnInit() {
    const transaction = window.history.state.transaction;
    if (transaction) {
      transaction.category = getCategoryNameInPortuguese(transaction.category);
      this.transaction = transaction;
      this.isEditMode = true;
    }
  }

  onSubmit() {
    if (!this.valid(this.transaction)) return;
    this.errorMessage = null;
    this.successMessage = null;
    this.isLoading = true;

    if (isIncome(this.transaction.type)) {
      if (this.isEditMode)
        return this.updateIncome();
      return this.addIncome();
    }
    if (isExpense(this.transaction.type)) {
      if (this.isEditMode)
        return this.updateExpense();
      return this.addExpense();
    }
    alert("Selecione um tipo de transação válido (Receita ou Despesa).");
    this.isLoading = false;
  }

  private addExpense() {
    this.transaction.category = getCategoryNameInEnglish(this.transaction.category);
    this.transactionService.addExpense(this.transaction).subscribe({
      next: (response) => {
        this.successMessage = 'Despesa adicionada com sucesso!';
        console.log('Expense added successfully:', response);
        this.transaction = emptyTransaction;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error adding expense:', error);
        this.errorMessage = error;
        this.successMessage = null;
        this.isLoading = false;
      }
    });
  }

  private updateExpense() {
    this.transaction.category = getCategoryNameInEnglish(this.transaction.category);
    this.transactionService.updateExpense(this.transaction).subscribe({
      next: async (response) => {
        this.successMessage = 'Despesa editada com sucesso!';
        console.log('Expense edited successfully:', response);
        await this.sleep(1);
        this.router.navigateByUrl("/dashboard");
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error editing expense:', error);
        this.errorMessage = error;
        this.successMessage = null;
        this.isLoading = false;
      }
    });
  }

  private addIncome() {
    this.transactionService.addIncome(this.transaction).subscribe({
      next: (response) => {
        this.successMessage = 'Receita adicionada com sucesso!';
        console.log('Income added successfully:', response);
        this.transaction = emptyTransaction;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error adding income:', error);
        this.errorMessage = error;
        this.successMessage = null;
        this.isLoading = false;
      }
    });
  }

  private updateIncome() {
    this.transactionService.updateIncome(this.transaction).subscribe({
      next: async (response) => {
        this.successMessage = 'Receita editada com sucesso!';
        console.log('Income edited successfully:', response);
        await this.sleep(1);
        this.router.navigateByUrl("/search");
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error editing income:', error);
        this.errorMessage = error;
        this.successMessage = null;
        this.isLoading = false;
      }
    });
  }

  sleep(seconds: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, seconds * 1000));
  }

  valid(transaction: Transaction) {
    if (!transaction.type || !transaction.value || !transaction.date || !transaction.description) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios.';
      this.successMessage = null;
      return false;
    }
    if (transaction.value < 1) {
      this.errorMessage = 'Se você precisa por um número negativo, está usando a categoria errada.';
      this.successMessage = null;
      return false;
    }
    if (transaction.value > 1000000000) {
      this.errorMessage = 'Quem você está querendo enganar?';
      this.successMessage = null;
      return false;
    }
    return true;
  }

  getCategoryNameInPortuguese(categoryName: string) {
    return getCategoryNameInPortuguese(categoryName);
  }
}
