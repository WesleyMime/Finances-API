import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { SummaryLastYear } from './summary-last-year';
import { SummaryByDate } from './summary-by-date';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {

  private readonly API_URL = 'http://localhost:8080'
  private readonly SUMMARY_ENDPOINT = '/summary';
  private readonly LASTYEAR_ENDPOINT = '/last-year';

  constructor(private http: HttpClient) { }

  getSummaryLastYear(date: Date): Observable<SummaryLastYear | any> {
    return this.http.get(this.API_URL + this.SUMMARY_ENDPOINT + this.LASTYEAR_ENDPOINT).pipe(
      catchError(this.handleError)
    );
  }
  getSummaryByDate(date: Date): Observable<SummaryByDate | any> {
    let fullUrl = this.API_URL + this.SUMMARY_ENDPOINT + "/" + date.getFullYear() + "/" + (date.getMonth());
    return this.http.get(fullUrl).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if(error.status == 422) errorMessage = "Falha no envio. Por favor verifique as informações.";
    return throwError(() => new Error(errorMessage));
  }
}
