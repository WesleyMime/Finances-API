import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let authService = inject(AuthService);
  let router = inject(Router);
  let authCookie = authService.getAuthCookie();
  if (!authService.isLoggedIn() && !router.url.includes("register")) {
    router.navigate(['/login']);
  }

  const newReq = req.clone({
    headers: req.headers.append('Authorization', "Bearer " + authCookie)
  });

  return next(newReq);
};
