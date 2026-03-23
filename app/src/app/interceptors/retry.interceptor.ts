import { HttpInterceptorFn } from '@angular/common/http';
import { timer, throwError } from 'rxjs';
import { timeout, retry, catchError } from 'rxjs/operators';

// Request timeout in ms and default retry settings
const DEFAULT_TIMEOUT = 5000; // 5s
const DEFAULT_RETRY_COUNT = 3;
const BASE_DELAY = 500; // base delay in ms

export const retryInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    timeout(DEFAULT_TIMEOUT),
    // use the retry operator with a delay function to implement exponential backoff
    // Note: RxJS retry accepts a config object with count and delay (delay returns an Observable)
    retry({
      count: DEFAULT_RETRY_COUNT,
      delay: (error: { status: any; statusCode: any; }, retryCount: number) => {        
        // If error is a client error (4xx except 429 Too Many Requests), do not retry
        const status = error.status as number;        
        if (status >= 400 && status < 500 && status !== 429 && status !== 404)
          return throwError(() => error);

        // exponential backoff (2^retryCount * base)
        const backoff = Math.pow(2, retryCount) * BASE_DELAY;
        return timer(backoff);
      }
    }),
    catchError((err) => {
      return throwError(() => err);
    })
  );
};
