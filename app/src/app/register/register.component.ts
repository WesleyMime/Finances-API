import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HeaderComponent } from "../header/header.component";

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink, HeaderComponent],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  name: string = '';
  email: string = '';
  passwordValue: string = '';
  confirmPasswordValue: string = '';

  onSubmit() {
    if (this.passwordValue !== this.confirmPasswordValue) {
      console.error('Passwords do not match!');
      return;
    }

    console.log('Registration attempted with:', {
      fullName: this.name,
      email: this.email,
      password: this.passwordValue,
    });
    // Add actual registration logic here (e.g., call an auth service)
  }
}