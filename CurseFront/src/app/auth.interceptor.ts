import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Всегда получаем токен из localStorage (на случай, если он еще не восстановлен в сервисе)
    const token = this.authService.getToken();
    
    // Проверяем, является ли это публичным эндпоинтом
    const isPublicEndpoint = request.url.includes('/auth/login') || 
                           request.url.includes('/auth/register');
    
    // Добавляем токен к запросам (кроме публичных эндпоинтов)
    if (token && !isPublicEndpoint) {
      const cloned = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      console.log('Adding token to request:', request.url);
      return next.handle(cloned).pipe(
        catchError((error: HttpErrorResponse) => {
          return this.handleAuthError(error, request);
        })
      );
    }
    
    // Если это не публичный эндпоинт и токена нет, это ошибка
    if (!isPublicEndpoint && !token) {
      console.warn('No token found for protected endpoint:', request.url);
      // Не делаем редирект здесь, пусть сервер вернет 401, и handleAuthError обработает
    }
    
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        return this.handleAuthError(error, request);
      })
    );
  }

  private handleAuthError(error: HttpErrorResponse, request: HttpRequest<unknown>): Observable<HttpEvent<unknown>> {
    // Если это ошибка 401 (Unauthorized) и это не публичный эндпоинт
    if (error.status === 401 && 
        !request.url.includes('/auth/login') && 
        !request.url.includes('/auth/register')) {
      
      // Очищаем токен и данные пользователя
      this.authService.logout();
      
      // Редиректим на страницу логина
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: this.router.url }
      });
    }
    
    return throwError(() => error);
  }
}

