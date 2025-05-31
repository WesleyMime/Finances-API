import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReportsService {

  private readonly API_URL = 'http://localhost:8080'
  private readonly SUMMARY_ENDPOINT = '/summary/last-year';

  constructor(private http: HttpClient) { }

  getSummary(date: any) {
    return this.http.get(this.API_URL + this.SUMMARY_ENDPOINT).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if(error.status == 422) errorMessage = "Falha no envio. Por favor verifique as informações.";
    return throwError(() => new Error(errorMessage));
  }
}
