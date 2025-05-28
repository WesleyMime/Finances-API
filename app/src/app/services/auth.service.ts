import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators'; // Used to side-effect (store token) without changing the observable
import { Router } from '@angular/router';
import { ILoginResponse, ILoginUser, IRegisterUser } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/auth';
  private loginEndpoint = '/login';
  private registerEndpoint = '/signin';

  private readonly TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: ILoginUser): Observable<ILoginResponse> {
    return this.http.post<ILoginResponse>(this.apiUrl+this.loginEndpoint, credentials).pipe(
      // Use tap to perform a side effect (storing the token)
      // without modifying the observable stream itself.
      tap(response => {
        if (response && response.token) {
          this.storeToken(response.token);
        }
      })
    );
  }

  private storeToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Checks if the user is currently logged in (based on the presence of a token).
   * NOTE: This is a basic check. A more robust check would validate the token's expiration
   *       or rely on a backend validation endpoint.
   * @returns True if a token is found in localStorage, false otherwise.
   */
  isLoggedIn(): boolean {
    const token = this.decodeToken();
    console.log(token);
    return !!token; // The !! converts the string/null to a boolean
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/']);
  }

  decodeToken(): any | null {
    const token = this.getToken();
    if (token) {
      try {
        const payload = token.split('.')[1];
        const decodedPayload = atob(payload); // atob decodes base64
        return JSON.parse(decodedPayload);
      } catch (e) {
        console.error('Failed to decode token:', e);
        return null;
      }
    }
    return null;
  }

  register(registerCredentials: IRegisterUser) {
    return this.http.post(this.apiUrl+this.registerEndpoint, registerCredentials).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Falha na requisição, tente novamente mais tarde.';
    if(error.status == 422) {
      errorMessage = "Falha no registro. Por favor verifique suas credenciais."
    }
    if(error.status == 409) {
      errorMessage = "Já existe um cliente cadastrado com esse email.";
    }
    return throwError(() => new Error(errorMessage));
  }
}
