import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Transaction } from './transaction';

@Injectable({
  providedIn: 'root'
})
export class AddTransactionService {

  private readonly API_URL = 'http://localhost:8080'
  private readonly INCOME_ENDPOINT = '/income';
  private readonly EXPENSE_ENDPOINT = '/expense';

  constructor(private http: HttpClient) { }

  addIncome(transaction: Transaction) {
    console.log('Adding income transaction:', transaction);
    
    return this.http.post(this.API_URL+this.INCOME_ENDPOINT, transaction).pipe(
      catchError(this.handleError)
    );
  }

  addExpense(transaction: Transaction) {
    console.log('Adding expense transaction:', transaction);
    
    return this.http.post(this.API_URL+this.EXPENSE_ENDPOINT, transaction).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if(error.status == 422) errorMessage = "Falha no envio. Por favor verifique as informações.";
    return throwError(() => new Error(errorMessage));
  }
}
