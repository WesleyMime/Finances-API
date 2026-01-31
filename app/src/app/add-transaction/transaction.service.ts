import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Transaction } from './transaction';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  private readonly API_URL = environment.API_URL
  private readonly INCOME_ENDPOINT = '/income';
  private readonly EXPENSE_ENDPOINT = '/expense';

  constructor(readonly http: HttpClient) { }

  addIncome(transaction: Transaction) {
    console.log('Adding income transaction:', transaction);

    return this.http.post(this.API_URL + this.INCOME_ENDPOINT, transaction).pipe(
      catchError(this.handleError)
    );
  }

  addIncomeList(transactions: any): Observable<any> {
    console.log('Adding income transactions:', transactions);
    let headers = new HttpHeaders().append("Content-Type", "application/json");
    return this.http.post(this.API_URL + this.INCOME_ENDPOINT + "/list", transactions, { headers }).pipe(
      catchError(this.handleError)
    );
  }


  addExpense(transaction: Transaction) {
    console.log('Adding expense transaction:', transaction);

    return this.http.post(this.API_URL + this.EXPENSE_ENDPOINT, transaction).pipe(
      catchError(this.handleError)
    );
  }

  addExpenseList(transactions: any): Observable<any> {
    console.log('Adding expenses transactions:', transactions);
    let headers = new HttpHeaders().append("Content-Type", "application/json");
    return this.http.post(this.API_URL + this.EXPENSE_ENDPOINT + "/list", transactions, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  updateIncome(transaction: Transaction) {
    console.log('Updating income:', transaction);

    return this.http.put(this.API_URL + this.INCOME_ENDPOINT + "/" + transaction.id, transaction).pipe(
      catchError(this.handleError)
    );
  }
  updateExpense(transaction: Transaction) {
    console.log('Updating expense:', transaction);

    return this.http.put(this.API_URL + this.EXPENSE_ENDPOINT + "/" + transaction.id, transaction).pipe(
      catchError(this.handleError)
    );
  }

  deleteIncome(id: number) {
    console.log('Deleting income:', id);

    let fullUrl = this.API_URL + this.INCOME_ENDPOINT + "/" + id;
    return this.http.delete(fullUrl).pipe(
      catchError(this.handleError)
    );
  }

  deleteExpense(id: number) {
    console.log('Deleting expense:', id);

    let fullUrl = this.API_URL + this.EXPENSE_ENDPOINT + "/" + id;
    return this.http.delete(fullUrl).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if (error.status == 422 || error.status == 400) errorMessage = "Falha no envio. Por favor verifique as informações.";
    if (error.status == 409) errorMessage = "Uma transação com esta descrição já foi registrada neste mês.";
    return throwError(() => new Error(errorMessage));
  }
}
