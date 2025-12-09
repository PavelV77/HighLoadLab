import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { ApiConfigService } from './api-config.service';

export interface LoginRequest {
  login: string;
  password: string;
}

export interface RegisterRequest {
  login: string;
  password: string;
  email?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  userId: string;
  login: string;
  email?: string;
}

export interface UserInfo {
  userId: string;
  login: string;
  email?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'user_info';
  private currentUserSubject = new BehaviorSubject<UserInfo | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService
  ) {
    // Восстанавливаем состояние из localStorage при инициализации
    this.initializeAuth();
  }

  private initializeAuth(): void {
    // Восстанавливаем пользователя из localStorage синхронно
    const userInfo = this.getUserFromStorage();
    const token = this.getToken();
    
    // Если есть токен и он не истек локально, восстанавливаем состояние сразу
    if (token && this.isTokenNotExpired(token)) {
      if (userInfo) {
        // Восстанавливаем состояние пользователя
        this.currentUserSubject.next(userInfo);
      } else {
        // Если есть токен, но нет данных пользователя, пытаемся восстановить из токена
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          const userInfoFromToken: UserInfo = {
            userId: payload.userId || '',
            login: payload.login || payload.sub || '',
            email: payload.email
          };
          localStorage.setItem(this.USER_KEY, JSON.stringify(userInfoFromToken));
          this.currentUserSubject.next(userInfoFromToken);
        } catch (e) {
          console.warn('Failed to restore user info from token:', e);
        }
      }
      // Валидируем токен на сервере в фоне (не блокируем восстановление состояния)
      this.validateTokenSilently();
    } else if (token) {
      // Токен истек - очищаем состояние
      console.log('Token expired, clearing auth state');
      this.logout();
    } else {
      // Токена нет - очищаем состояние
      this.currentUserSubject.next(null);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      this.apiConfig.getApiUrl('/auth/login'),
      credentials
    ).pipe(
      tap(response => this.handleAuthResponse(response))
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      this.apiConfig.getApiUrl('/auth/register'),
      data
    ).pipe(
      tap(response => this.handleAuthResponse(response))
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token || token === '') {
      return false;
    }
    
    // Проверяем, не истек ли токен локально (без запроса на сервер)
    return this.isTokenNotExpired(token);
  }

  private isTokenNotExpired(token: string): boolean {
    try {
      // Декодируем JWT токен (без проверки подписи, только для получения expiration)
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiration = payload.exp * 1000; // exp в секундах, преобразуем в миллисекунды
      const now = Date.now();
      
      // Проверяем, что токен не истек (с запасом в 1 минуту)
      return expiration > (now + 60000);
    } catch (e) {
      // Если не удалось декодировать токен, считаем его невалидным
      console.warn('Failed to decode token:', e);
      return false;
    }
  }

  getCurrentUser(): UserInfo | null {
    return this.currentUserSubject.value;
  }

  private handleAuthResponse(response: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    const userInfo: UserInfo = {
      userId: response.userId,
      login: response.login,
      email: response.email
    };
    localStorage.setItem(this.USER_KEY, JSON.stringify(userInfo));
    this.currentUserSubject.next(userInfo);
  }

  private getUserFromStorage(): UserInfo | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    if (userStr) {
      try {
        return JSON.parse(userStr);
      } catch {
        return null;
      }
    }
    return null;
  }

  private validateTokenSilently(): void {
    const token = this.getToken();
    if (!token) {
      return;
    }

    // Валидируем токен через API в фоне (без блокировки UI)
    // Если валидация не удалась, не очищаем состояние сразу - пусть это сделает interceptor при реальном запросе
    this.http.get(this.apiConfig.getApiUrl('/auth/validate'), {
      headers: { 'Authorization': `Bearer ${token}` }
    }).subscribe({
      next: () => {
        // Токен валиден на сервере
        console.log('Token validated successfully on server');
      },
      error: (err) => {
        // Токен невалиден на сервере, но не очищаем сразу
        // Interceptor очистит при реальном запросе с 401
        console.warn('Token validation failed on server:', err);
        // Не вызываем logout() здесь, чтобы не сбрасывать состояние при обновлении страницы
      }
    });
  }
}

