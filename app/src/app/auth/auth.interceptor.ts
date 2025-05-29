import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let authCookie = inject(AuthService).getAuthCookie();
  console.log("authInterceptor " + authCookie);

  const newReq = req.clone({
    headers: req.headers.append('Authorization', "Bearer " + authCookie)
  });

  return next(newReq);
};
