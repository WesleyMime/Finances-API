import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private readonly API_URL = environment.API_URL;
  private readonly INCOME_ENDPOINT = "/income";
  private readonly EXPENSE_ENDPOINT = "/expense";

  constructor(readonly http: HttpClient) { }

  searchIncomeByDate(date: string): Observable<any> {
    return this.http.get(this.API_URL + this.INCOME_ENDPOINT + "/" + date).pipe(
      catchError(this.handleError)
    );
  }

  searchExpenseByDate(date: string): Observable<any> {
    return this.http.get(this.API_URL + this.EXPENSE_ENDPOINT + "/" + date).pipe(
      catchError(this.handleError)
    );
  }

  searchIncomeByDescription(description: string | null, lastId: number | null, lastDate: string | null): Observable<any> {
    let params = new HttpParams();
    params = this.getParams(description, lastId, lastDate, params);
    return this.http.get(this.API_URL + this.INCOME_ENDPOINT, { params }).pipe(
      catchError(this.handleError)
    );
  }

  searchExpenseByDescription(description: string | null, lastId: number | null, lastDate: string | null): Observable<any> {
    let params = new HttpParams();
    params = this.getParams(description, lastId, lastDate, params);
    return this.http.get(this.API_URL + this.EXPENSE_ENDPOINT, { params }).pipe(
      catchError(this.handleError)
    );
  }

  private getParams(description: string | null, lastId: number | null, lastDate: string | null, params: HttpParams) {
    if (description)
      params = params.set("description", description);
    if (lastId && lastDate) {
      params = params.set("lastId", lastId);
      params = params.set("lastDate", lastDate);
    }
    return params;
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if (error.status == 404) errorMessage = "Nenhuma transação encontrada.";
    return throwError(() => new Error(errorMessage));
  }
}
