import { Component, NgModule } from '@angular/core';
import { FormsModule, NgModel } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HeaderComponent } from "../header/header.component";
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink, HeaderComponent, NgIf],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  passwordVisible: boolean = false;

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }

  email: string = '';
  passwordValue: string = '';

  onSubmit() {
    console.log('Login attempted with:', this.email, this.passwordValue);
    // Add actual login logic here (e.g., call an auth service)
  }
}