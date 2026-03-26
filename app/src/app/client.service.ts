import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from './environments/environment';
import { Observable, of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private readonly API_URL = environment.API_URL
  private readonly CLIENT_ENDPOINT = '/client';

  private client: any = null;

  constructor(readonly http: HttpClient) { }

  getClient(): Observable<any> {
    if (this.client) {
      return of(this.client);
    }
    return this.http.get(this.API_URL + this.CLIENT_ENDPOINT).pipe(
      tap(result => {
        this.client = result;
      }),
      catchError(this.handleError)
    );
  }

  removeClient(): void {    
    this.client = null;
  }

  patchClient(client: any): Observable<any> {
    return this.http.patch(this.API_URL + this.CLIENT_ENDPOINT, client).pipe(
      tap(result => this.client = result),
      catchError(this.handleError)
    );
  }

  deleteClient(): Observable<any> {
    return this.http.delete(this.API_URL + this.CLIENT_ENDPOINT).pipe(
      tap(result => this.client = result),
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if (error.status == 422) {
      let msg = error.error.errors[0].detail;
      errorMessage = msg.charAt(0).toUpperCase() + msg.slice(1);
    }
    return throwError(() => new Error(errorMessage));
  }
}
