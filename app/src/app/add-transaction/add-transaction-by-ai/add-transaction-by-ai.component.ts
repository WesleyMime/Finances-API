import { Component, inject } from '@angular/core';
import { emptyTransaction, Transaction } from '../transaction';
import { AiService } from '../../reports/ai.service';
import { TransactionService } from '../transaction.service';
import { ChatResponse } from '../../reports/chat-response';
import { HeaderComponent } from '../../header/header.component';
import { FormsModule } from '@angular/forms';
import { isExpense, isIncome, transactionTypeEnum } from '../transaction-types';
import { LoadingValueComponent } from '../../loading-value/loading-value.component';

@Component({
  selector: 'app-add-transaction-by-ai',
  imports: [FormsModule, HeaderComponent, LoadingValueComponent],
  templateUrl: './add-transaction-by-ai.component.html',
  styleUrl: './add-transaction-by-ai.component.css'
})
export class AddTransactionByAiComponent {
  transaction: Transaction = JSON.parse(JSON.stringify(emptyTransaction));

  transactionPrompt: Transaction = JSON.parse(JSON.stringify(emptyTransaction));
  successMessageIA: string | null = null;
  errorMessageIA: string | null = null;
  isLoading: boolean = false;

  transactionTypes = transactionTypeEnum

  aiService = inject(AiService);
  transactionService = inject(TransactionService);

  submitPrompt() {
    if (!this.validPrompt(this.transactionPrompt)) {
      return;
    }
    this.isLoading = true;
    this.errorMessageIA = null;
    this.aiService.getJSONForTransactionsUsingAI(this.transactionPrompt.description, this.transactionPrompt.type).subscribe({
      next: (response: ChatResponse) => {
        if (isIncome(this.transactionPrompt.type)) {
          return this.addIncomeList(response.message);
        }
        if (isExpense(this.transactionPrompt.type)) {
          return this.addExpenseList(response.message);
        }
        alert("Selecione um tipo de transação válido (Receita ou Despesa).");
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error adding transaction:', error);
        this.errorMessageIA = error.message;
        this.successMessageIA = null;
        this.isLoading = false;
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
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error adding expense:', error);
        this.errorMessageIA = error;
        this.successMessageIA = null;
        this.isLoading = false;
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
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error adding income:', error);
        this.errorMessageIA = error;
        this.successMessageIA = null;
        this.isLoading = false;
      }
    });
  }

  validPrompt(transaction: Transaction) {
    if (!transaction.type || !transaction.description) {
      this.errorMessageIA = 'Por favor, preencha todos os campos obrigatórios.';
      this.successMessageIA = null;
      return false;
    }
    return true;
  }
}
