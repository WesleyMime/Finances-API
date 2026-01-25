import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators'; // Used to side-effect (store token) without changing the observable
import { Router } from '@angular/router';
import { ILoginResponse, ILoginUser, IRegisterUser } from './user.model';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  readonly apiUrl = environment.API_URL + "/auth";
  readonly loginEndpoint = '/login';
  readonly registerEndpoint = '/signin';

  private readonly TOKEN_KEY = 'auth_token';
  private readonly EXPIRATION_MINUTES = 30;

  constructor(readonly http: HttpClient, readonly router: Router) { }

  login(credentials: ILoginUser): Observable<ILoginResponse> {
    return this.http.post<ILoginResponse>(this.apiUrl + this.loginEndpoint, credentials).pipe(
      // Use tap to perform a side effect (storing the token)
      // without modifying the observable stream itself.
      tap(response => {
        if (response?.token) {
          this.setCookie(this.TOKEN_KEY, response.token);
        }
      })
    );
  }

  setCookie(name: string, value: string) {
    let date = new Date();
    let minutesInMilliseconds = this.EXPIRATION_MINUTES * 60 * 1000;
    date.setTime(date.getTime() + minutesInMilliseconds);

    localStorage.setItem(name, value);
  }

  getAuthCookie(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.decodeToken();
    if (!token) return false;

    token.exp = token.exp * 1000; // Convert to milliseconds
    if (token.exp < Date.now()) {
      console.warn("Token expired");
      this.logout();
      return false;
    }
    console.log("Time remaining until token expiration (min):", Math.floor((token.exp - Date.now()) / 1000 / 60));
    return true;
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/']);
  }

  decodeToken(): any {
    const token = this.getAuthCookie();
    if (!token) {
      console.warn('No token found');
      return;
    }
    try {
      const payload = token.split('.')[1];
      const decodedPayload = atob(payload); // atob decodes base64
      return JSON.parse(decodedPayload);
    } catch (e) {
      console.error('Failed to decode token:', e);
      return;
    }
  }

  register(registerCredentials: IRegisterUser) {
    return this.http.post(this.apiUrl + this.registerEndpoint, registerCredentials).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if (error.status == 422) errorMessage = "Falha no registro. Por favor verifique suas credenciais.";
    if (error.status == 409) errorMessage = "Já existe um cliente cadastrado com esse email.";
    return throwError(() => new Error(errorMessage));
  }
}
