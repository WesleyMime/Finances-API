import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators'; // Used to side-effect (store token) without changing the observable
import { Router } from '@angular/router';

interface LoginCredentials {
  email: string;
  password: string;
}

interface LoginResponse {
  token: string;
  type: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/auth/login';

  private readonly TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: LoginCredentials): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.apiUrl, credentials).pipe(
      // Use tap to perform a side effect (storing the token)
      // without modifying the observable stream itself.
      tap(response => {
        if (response && response.token) {
          this.storeToken(response.token);
        } else {
          console.error('Login successful but no token received in response.');
          throw new Error('No token received');
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
}
