import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {

  private readonly API_URL = environment.API_URL
  private readonly SUMMARY_ENDPOINT = '/summary';
  private readonly LAST_YEAR_ENDPOINT = '/last-year';

  constructor(readonly http: HttpClient) { }

  getSummaryLastYear(date: Date): Observable<any> {
    return this.http.get(this.API_URL + this.SUMMARY_ENDPOINT + this.LAST_YEAR_ENDPOINT).pipe(
      catchError(this.handleError)
    );
  }
  getSummaryByDate(date: Date): Observable<any> {
    let fullUrl = this.API_URL + this.SUMMARY_ENDPOINT + "/" + date.getFullYear() + "/" + (date.getMonth() + 1);
    return this.http.get(fullUrl).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if (error.status == 422) errorMessage = "Falha no envio. Por favor verifique as informações.";
    return throwError(() => new Error(errorMessage));
  }
}
