import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { map, take, tap } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    // Проверяем авторизацию через Observable, чтобы учитывать изменения состояния
    return this.authService.currentUser$.pipe(
      take(1),
      map(user => {
        // Если пользователь авторизован (есть токен и данные пользователя)
        if (user !== null && this.authService.isAuthenticated()) {
          return true;
        }
        
        // Если не авторизован, редиректим на страницу логина с сохранением URL
        this.router.navigate(['/auth/login'], {
          queryParams: { returnUrl: state.url }
        });
        return false;
      })
    );
  }
}

