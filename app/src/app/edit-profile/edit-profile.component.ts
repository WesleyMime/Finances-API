import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from "../header/header.component";
import { ClientService } from '../client.service';
import { Client } from '../client';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-edit-profile',
  imports: [FormsModule, HeaderComponent],
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit{
  
  passwordVisible: boolean = false;
  clientService = inject(ClientService);
  authService = inject(AuthService);
  
  errorMessageName: string | undefined;
  errorMessageEmail: string | undefined;
  errorMessagePassword: string | undefined;
  successMessageName: string | undefined;
  successMessageEmail: string | undefined;
  successMessagePassword: string | undefined;
  
  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }
  
  ngOnInit(): void {
    this.clientService.getClient().subscribe((result) => {
      this.client = result;
    });
  }

  currentPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';

  client: Client = {name: 'Nome', email: 'Email', password: ''};
  form: Client = {name: '', email: '', password: ''};

  saveFullName(): void {
    this.clientService.patchClient({name: this.form.name}).subscribe({
      next: (response: Client) => {
        this.client.name = response.name;
        this.form.name = '';
        this.errorMessageName = undefined;
        this.successMessageName = "Nome atualizado com sucesso";
      },
      error: (error: Error) => {
        this.successMessageName = undefined;
        this.errorMessageName = error.message;
      }
    });
  }

  saveEmailAddress(): void {
    this.clientService.patchClient({email: this.form.email}).subscribe({
      next: (response: Client) => {
        this.client.email = response.email;
        this.form.email = '';
        this.errorMessageEmail = undefined;
        this.successMessageEmail = "Email atualizado com sucesso";
      },
      error: (error: Error) => {
        this.successMessageEmail = undefined;
        this.errorMessageEmail = error.message;
      }
    });
  }

  savePassword(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessagePassword = 'Senhas não coincidem!';
      return;
    }
    if (this.currentPassword.length == 0) {
      this.errorMessagePassword = 'Senha atual é obrigatória!';
      return;
    }
      
    this.authService.login({email: this.client.email, password: this.currentPassword}).subscribe({
      next: () => {
        this.clientService.patchClient({password: this.newPassword}).subscribe({
          next: () => {
            this.errorMessagePassword = undefined;
            this.successMessagePassword = "Senha atualizada com sucesso";
          },
          error: (error: Error) => {
            this.successMessagePassword = undefined;
            this.errorMessagePassword = error.message;
          }
        });
      },
      error: () => {
        this.successMessagePassword = undefined;
        this.errorMessagePassword = 'Senha atual incorreta';
      }
    });
  }
}
