import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Router, CanActivateFn } from '@angular/router';
import { ClientService } from '../client.service';
import { Client } from '../client';

export const authGuard: CanActivateFn = (route, state) => {
  let authService = inject(AuthService);
  let clientService = inject(ClientService);
  if (!authService.isLoggedIn()) {
    let router = inject(Router);
    router.navigate(['/login']);
    return false;
  }
  if (!currentClient) {
    clientService.getClient().subscribe((result: Client) => {
      currentClient = result;
    });
  }
  return true;
};

export let currentClient: Client;
