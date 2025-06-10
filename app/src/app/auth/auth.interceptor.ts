import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  var authService = inject(AuthService);
  let authCookie = authService.getAuthCookie();
  if (!authService.isLoggedIn()) {
    let router = inject(Router);
    router.navigate(['/login']);
  }

  const newReq = req.clone({
    headers: req.headers.append('Authorization', "Bearer " + authCookie)
  });

  return next(newReq);
};
