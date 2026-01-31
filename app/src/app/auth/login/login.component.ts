import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { HeaderComponent } from '../../header/header.component';
import { AuthService } from '../auth.service';
import { ILoginUser } from '../user.model';

@Component({
  selector: 'app-login',
  imports: [FormsModule, HeaderComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(readonly authService: AuthService, readonly router: Router) { }
  passwordVisible: boolean = false;
  isLoading: boolean = false

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }

  credentials: ILoginUser = { email: '', password: '' };
  errorMessage: string | null = null;

  onSubmit() {
    this.isLoading = true;
    console.log('Login attempted with:', this.credentials.email);
    this.errorMessage = null;
    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        console.log('Login successful', response);
        this.isLoading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (error: Error) => {
        console.error('Login failed', error);
        this.isLoading = false;
        this.errorMessage = error.message;
      }
    });
  }

  onLogout(): void {
    this.authService.logout();
    console.log('Logged out');
    this.router.navigate(['/']);
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

}
