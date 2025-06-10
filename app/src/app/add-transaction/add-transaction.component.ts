import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../header/header.component";
import { AddTransactionService } from './add-transaction.service';
import { Transaction } from './transaction';

@Component({
  selector: 'app-add-transaction',
  imports: [FormsModule, HeaderComponent],
  templateUrl: './add-transaction.component.html',
  styleUrls: ['./add-transaction.component.css']
})
export class AddTransactionComponent {
  transaction: Transaction = {
    type: '',
    category: '',
    value: 0,
    date: '',
    description: ''
  }

  sucessMessage: string | null = null;
  errorMessage: string | null = null;

  transactionTypes: string[] = ['Receita', 'Despesa'];
	categoryForm: {index: number, name: string}[] = [
    {index: 0, name: 'Alimentação'},
    {index: 1, name: 'Saúde'},
    {index: 2, name: 'Casa'},
    {index: 3, name: 'Transporte'},
    {index: 4, name: 'Educação'},
    {index: 5, name: 'Lazer'},
    {index: 6, name: 'Imprevístos'},
    {index: 7, name: 'Outros'}
  ];

  categoryEnum: {index: number, name: string}[] = [
    {index: 0, name: 'Food'},
    {index: 1, name: 'Health'},
    {index: 2, name: 'Home'},
    {index: 3, name: 'Transport'},
    {index: 4, name: 'Education'},
    {index: 5, name: 'Leisure'},
    {index: 6, name: 'Unforeseen'},
    {index: 7, name: 'Others'}
  ];

  transactionService = inject(AddTransactionService);

  onSubmit() {
    if (!this.transaction.type || !this.transaction.value ||
      !this.transaction.date || !this.transaction.description) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios.';
      return;
    }
    this.errorMessage = null;

    if (this.transaction.type == this.transactionTypes[0]) { // Receita
      this.transactionService.addIncome(this.transaction).subscribe({
        next: (response) => {
          this.sucessMessage = 'Receita adicionada com sucesso!';
          console.log('Income added successfully:', response);
          this.resetForm();
        },
        error: (error) => {
          console.error('Error adding income:', error);
          this.errorMessage = error;
          this.sucessMessage = null;
        }
      });
      return;
    }
    else if (this.transaction.type == this.transactionTypes[1]) { // Despesa
      this.transaction.category = this.categoryEnum.at(Number.parseInt(this.transaction.category))?.name || 'Other';
      this.transactionService.addExpense(this.transaction).subscribe({
        next: (response) => {
          this.sucessMessage = 'Despesa adicionada com sucesso!';
          console.log('Expense added successfully:', response);
          this.resetForm();
        },
        error: (error) => {
          console.error('Error adding expense:', error);
          this.errorMessage = error;
          this.sucessMessage = null;
        }
      });
      return;
    }
    alert("Selecione um tipo de transação válido (Receita ou Despesa).");
    return;
  }

  resetForm() {
    this.transaction.type = '';
    this.transaction.category = '';
    this.transaction.value = 0;
    this.transaction.date = '';
    this.transaction.description = '';
    this.errorMessage = null;
  }
}
