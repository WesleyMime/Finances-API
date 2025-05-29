import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  let authService = inject(AuthService);
  if (!authService.isLoggedIn()) {
    let router = inject(Router);
    router.navigate(['/login']);
    return false;
  }
  return true;
};
