import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let authCookie = inject(AuthService).getAuthCookie();
  console.log("authInterceptor" + authCookie);

  const newReq = req.clone({
    headers: req.headers.append('Authorization', authCookie)
  });
  
  return next(newReq);
};
