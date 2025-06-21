import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../header/header.component";
import { TransactionService } from './transaction.service';
import { emptyTransaction, Transaction } from './transaction';
import { Router } from '@angular/router';
import { categoriesEnum, getCategoryNameInEnglish, getCategoryNameInPortuguese } from '../category';

@Component({
  selector: 'app-add-transaction',
  imports: [FormsModule, HeaderComponent],
  templateUrl: './add-transaction.component.html',
  styleUrls: ['./add-transaction.component.css']
})
export class AddTransactionComponent implements OnInit {
  isEditMode = false;
  transaction: Transaction = emptyTransaction;

  sucessMessage: string | null = null;
  errorMessage: string | null = null;

  transactionTypes: string[] = ['Receita', 'Despesa'];

  transactionService = inject(TransactionService);
  router = inject(Router);
  categories = categoriesEnum;

  ngOnInit() {
    const transaction = window.history.state.transaction;
    if (transaction) {
      this.transaction = transaction;
      this.isEditMode = true;
    }
  }

  onSubmit() {
    if (!this.valid(this.transaction)) return;
    this.errorMessage = null;

    if (this.isIncome()) {
      if (this.isEditMode)
        return this.updateIncome();
      return this.addIncome();
    }
    if (this.isExpense()) {
      if (this.isEditMode)
        return this.updateExpense();
      return this.addExpense();
    }
    alert("Selecione um tipo de transação válido (Receita ou Despesa).");
    return;

  }

  private isExpense() {
    return this.transaction.type == this.transactionTypes[1];
  }

  private addExpense() {
    this.transactionService.addExpense(this.transaction).subscribe({
      next: (response) => {
        this.sucessMessage = 'Despesa adicionada com sucesso!';
        console.log('Expense added successfully:', response);
        this.transaction = emptyTransaction;
      },
      error: (error) => {
        console.error('Error adding expense:', error);
        this.errorMessage = error;
        this.sucessMessage = null;
      }
    });
  }

  private updateExpense() {
    this.transaction.category = getCategoryNameInEnglish(this.transaction.category);
    this.transactionService.updateExpense(this.transaction).subscribe({
      next: async (response) => {
        this.sucessMessage = 'Despesa editada com sucesso!';
        console.log('Expense edited successfully:', response);
        await this.sleep(1);
        this.router.navigateByUrl("/dashboard");
      },
      error: (error) => {
        console.error('Error editing expense:', error);
        this.errorMessage = error;
        this.sucessMessage = null;
      }
    });
  }

  private isIncome() {
    return this.transaction.type == this.transactionTypes[0];
  }

  private addIncome() {
    this.transactionService.addIncome(this.transaction).subscribe({
      next: (response) => {
        this.sucessMessage = 'Receita adicionada com sucesso!';
        console.log('Income added successfully:', response);
        this.transaction = emptyTransaction;
      },
      error: (error) => {
        console.error('Error adding income:', error);
        this.errorMessage = error;
        this.sucessMessage = null;
      }
    });
  }

  private updateIncome() {
    this.transactionService.updateIncome(this.transaction).subscribe({
      next: async (response) => {
        this.sucessMessage = 'Receita editada com sucesso!';
        console.log('Income edited successfully:', response);
        await this.sleep(1);
        this.router.navigateByUrl("/search");
      },
      error: (error) => {
        console.error('Error editing income:', error);
        this.errorMessage = error;
        this.sucessMessage = null;
      }
    });
  }

  sleep(seconds: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, seconds * 1000));
  }

  valid(transaction: Transaction) {
    if (!transaction.type || !transaction.value || !transaction.date || !transaction.description) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios.';
      return false;
    }
    if (transaction.value < 1) {
      this.errorMessage = 'Por favor, digite um número maior que zero.'
      return false;
    }
    return true;
  }
}
