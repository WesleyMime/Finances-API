import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../environments/environment';
import { Transaction } from '../add-transaction/transaction';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private readonly API_URL = environment.API_URL;
  private readonly INCOME_ENDPOINT = "/income";
  private readonly EXPENSE_ENDPOINT = "/expense";

  constructor(private http: HttpClient) { }

  searchIncomeByDate(year: number, month: number): Observable<Transaction[] | any> {
    return this.http.get(this.API_URL + this.INCOME_ENDPOINT + "/" + year+ "/" + month).pipe(
      catchError(this.handleError)
    );
  }

  searchExpenseByDate(year: number, month: number): Observable<Transaction[] | any> {
    return this.http.get(this.API_URL + this.EXPENSE_ENDPOINT + "/" + year+ "/" + month).pipe(
      catchError(this.handleError)
    );
  }

  searchIncomeByDescription(description: string): Observable<Transaction[] | any> {
    return this.http.get(this.API_URL + this.INCOME_ENDPOINT + "?description=" + description).pipe(
      catchError(this.handleError)
    );
  }

  searchExpenseByDescription(description: string): Observable<Transaction[] | any> {
    return this.http.get(this.API_URL + this.EXPENSE_ENDPOINT + "?description=" + description).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if(error.status == 404) errorMessage = "Nenhuma transação encontrada.";
    return throwError(() => new Error(errorMessage));
  }
}
