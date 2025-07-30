import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../header/header.component";
import { TransactionService } from './transaction.service';
import { emptyTransaction, Transaction } from './transaction';
import { Router } from '@angular/router';
import { categoriesEnum, getCategoryNameInEnglish, getCategoryNameInPortuguese } from '../category';
import { AiService } from '../reports/ai.service';
import { ChatResponse } from '../reports/chat-response';

@Component({
  selector: 'app-add-transaction',
  imports: [FormsModule, HeaderComponent],
  templateUrl: './add-transaction.component.html',
  styleUrls: ['./add-transaction.component.css']
})
export class AddTransactionComponent implements OnInit {
  isEditMode = false;
  transaction: Transaction = JSON.parse(JSON.stringify(emptyTransaction)); // Copy without reference
  transactionPrompt: Transaction = JSON.parse(JSON.stringify(emptyTransaction));

  successMessage: string | null = null;
  successMessageIA: string | null = null;
  errorMessage: string | null = null;
  errorMessageIA: string | null = null;

  transactionTypes: string[] = ['Receita', 'Despesa'];

  transactionService = inject(TransactionService);
  router = inject(Router);
  aiService = inject(AiService);
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

    if (this.isIncome(this.transaction.type)) {
      if (this.isEditMode)
        return this.updateIncome();
      return this.addIncome();
    }
    if (this.isExpense(this.transaction.type)) {
      if (this.isEditMode)
        return this.updateExpense();
      return this.addExpense();
    }
    alert("Selecione um tipo de transação válido (Receita ou Despesa).");
  }

  submitPrompt() {
    if (!this.validPrompt(this.transactionPrompt)) return;
    this.errorMessageIA = null;
    this.successMessageIA = "Processando...";
    this.aiService.getJSONForTransactionsUsingAI(this.transactionPrompt.description, this.transactionPrompt.type).subscribe({
      next: (response: ChatResponse) => {
        if (this.isIncome(this.transactionPrompt.type)) {
          return this.addIncomeList(response.message);
        }
        if (this.isExpense(this.transactionPrompt.type)) {
          return this.addExpenseList(response.message);
        }
        alert("Selecione um tipo de transação válido (Receita ou Despesa).");
      },
      error: (error: any) => {
        console.error('Error adding transaction:', error);
        this.errorMessageIA = error.message;
        this.successMessageIA = null;
      }
    });
  }

  private isExpense(type: string) {
    return type == this.transactionTypes[1];
  }

  private addExpense() {
    this.transaction.category = getCategoryNameInEnglish(this.transaction.category);
    this.transactionService.addExpense(this.transaction).subscribe({
      next: (response) => {
        this.successMessage = 'Despesa adicionada com sucesso!';
        console.log('Expense added successfully:', response);
        this.transaction = emptyTransaction;
      },
      error: (error) => {
        console.error('Error adding expense:', error);
        this.errorMessage = error;
        this.successMessage = null;
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
      },
      error: (error) => {
        console.error('Error editing expense:', error);
        this.errorMessage = error;
        this.successMessage = null;
      }
    });
  }

  private addExpenseList(list: string) {
    this.transactionService.addExpenseList(list).subscribe({
      next: (response: Transaction[]) => {
        this.successMessageIA = response.length != 1 ? response.length + ' despesas adicionadas!'
          : '1 despesa adicionada!';
        console.log('Expense added successfully:', response);
        this.transaction = emptyTransaction;
      },
      error: (error) => {
        console.error('Error adding expense:', error);
        this.errorMessageIA = error;
        this.successMessageIA = null;
      }
    });
  }

  private isIncome(type: string) {
    return type == this.transactionTypes[0];
  }

  private addIncome() {
    this.transactionService.addIncome(this.transaction).subscribe({
      next: (response) => {
        this.successMessage = 'Receita adicionada com sucesso!';
        console.log('Income added successfully:', response);
        this.transaction = emptyTransaction;
      },
      error: (error) => {
        console.error('Error adding income:', error);
        this.errorMessage = error;
        this.successMessage = null;
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
      },
      error: (error) => {
        console.error('Error editing income:', error);
        this.errorMessage = error;
        this.successMessage = null;
      }
    });
  }

  private addIncomeList(list: string) {
    this.transactionService.addIncomeList(list).subscribe({
      next: (response: Transaction[]) => {
        this.successMessageIA = response.length != 1 ? response.length + ' receitas adicionadas!'
          : '1 receita adicionada!';
        console.log('Income added successfully:', response);
        this.transaction = emptyTransaction;
      },
      error: (error) => {
        console.error('Error adding income:', error);
        this.errorMessageIA = error;
        this.successMessageIA = null;
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
      this.errorMessage = 'Por favor, digite um número maior que zero.'
      this.successMessage = null;
      return false;
    }
    return true;
  }

  validPrompt(transaction: Transaction) {
    if (!transaction.type || !transaction.description) {
      this.errorMessageIA = 'Por favor, preencha todos os campos obrigatórios.';
      this.successMessageIA = null;
      return false;
    }
    return true;
  }

  getCategoryNameInPortuguese(categoryName: string) {
    return getCategoryNameInPortuguese(categoryName);
  }
}
