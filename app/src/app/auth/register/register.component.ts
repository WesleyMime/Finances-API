import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { HeaderComponent } from '../../header/header.component';
import { AuthService } from '../auth.service';
import { IRegisterUser } from '../user.model';

@Component({
  selector: 'app-register',
  imports: [FormsModule, HeaderComponent],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  constructor(
    readonly authService: AuthService,
    readonly router: Router,
  ) {}

  credentials: IRegisterUser = {email: '', name: '', password: '', confirmPassword: ''};
  passwordVisible: boolean = false;
  errorMessage: string | null = null;

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }


  onSubmit() {
    if (this.credentials.password !== this.credentials.confirmPassword) {
      this.errorMessage = ('Senhas não coincidem!');
      return;
    }

    console.log('Registration attempted with:', {
      fullName: this.credentials.name,
      email: this.credentials.email
    });
    this.authService.register(this.credentials).subscribe({
      next: (response: any) => {
        console.log('Register successful', response);
        alert('Registrado com sucesso!');
        this.router.navigate(['/login']);
      },
      error: (error: Error) => {
        console.log('Register failed', error);
        this.errorMessage = error.message;
      }
    });
  }
}
