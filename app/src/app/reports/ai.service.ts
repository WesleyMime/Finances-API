import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AiService {

  private readonly API_URL = environment.API_URL
  private readonly AI_ENDPOINT = '/ai';

  constructor(readonly http: HttpClient) { }

  getMonthOverMonthComparisonTakeaway(income: number, expenses: number): Observable<any> {
    let params = new HttpParams().set("income", income).set("expenses", expenses);
    return this.http.get(this.API_URL + this.AI_ENDPOINT + "/monthOverMonth", { params }).pipe(
      catchError(this.handleError)
    );
  }

  getFinancialBalanceTakeaway(listBalanceEachMonth: string): Observable<any> {
    return this.http.post(this.API_URL + this.AI_ENDPOINT + "/financialBalance", listBalanceEachMonth).pipe(
      catchError(this.handleError)
    );
  }

  getSpendingByCategoryLastMonthTakeaway(spendingByCategoryMonth: any): Observable<any> {
    return this.http.post(this.API_URL + this.AI_ENDPOINT + "/spendingByCategoryLastMonth", spendingByCategoryMonth).pipe(
      catchError(this.handleError)
    );
  }

  getSpendingByCategoryYearTakeaway(spendingByCategoryYear: any): Observable<any> {
    return this.http.post(this.API_URL + this.AI_ENDPOINT + "/spendingByCategoryYear", spendingByCategoryYear).pipe(
      catchError(this.handleError)
    );
  }

  getSavingsTakeaway(savings: string): Observable<any> {
    let params = new HttpParams().set("savingsPercentage", savings)
    return this.http.get(this.API_URL + this.AI_ENDPOINT + "/savings", { params }).pipe(
      catchError(this.handleError)
    );
  }

  getJSONForTransactionsUsingAI(transaction: string, type: string): Observable<any> {
    console.log('Adding transaction: ', type, transaction);
    let params = new HttpParams().set("type", type);
    return this.http.post(this.API_URL + this.AI_ENDPOINT + "/jsonForTransactions", transaction, { params }).pipe(
      catchError(this.handleError)
    );
  }


  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if (error.status == 422) errorMessage = "Falha no envio. Por favor verifique as informações.";
    return throwError(() => new Error(errorMessage));
  }
}
