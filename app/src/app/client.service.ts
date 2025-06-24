import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from './environments/environment';
import { Observable, catchError, throwError } from 'rxjs';
import { Client } from './client';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private readonly API_URL = environment.API_URL
  private readonly CLIENT_ENDPOINT = '/client';

  constructor(private http: HttpClient) { }

  getClient(): Observable<Client | any> {
    return this.http.get(this.API_URL + this.CLIENT_ENDPOINT).pipe(
      catchError(this.handleError)
    );
  }

  patchClient(client: any): Observable<Client | any> {
    return this.http.patch(this.API_URL + this.CLIENT_ENDPOINT, client).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if (error.status == 422) {
      var msg = error.error.errors[0].detail;
      errorMessage = msg.charAt(0).toUpperCase() + msg.slice(1);
    }
    return throwError(() => new Error(errorMessage));
  }
}
