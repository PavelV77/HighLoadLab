import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, map } from 'rxjs';
import { take } from 'rxjs/operators';
import { AuthService, UserInfo } from './auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'CurseFront';
  isAuthenticated$: Observable<boolean>;
  currentUser$: Observable<UserInfo | null>;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
    this.isAuthenticated$ = this.authService.currentUser$.pipe(
      map(user => user !== null)
    );
    this.currentUser$ = this.authService.currentUser$;
  }

  ngOnInit(): void {
    // При инициализации проверяем авторизацию
    // Если пользователь не авторизован и находится не на странице логина/регистрации,
    // редиректим на логин
    this.authService.currentUser$.pipe(
      take(1)
    ).subscribe(user => {
      const currentPath = this.router.url;
      const isPublicPath = currentPath.startsWith('/auth/login') || 
                          currentPath.startsWith('/auth/register');
      
      if (!user && !isPublicPath && currentPath !== '/') {
        this.router.navigate(['/auth/login'], {
          queryParams: { returnUrl: currentPath }
        });
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(["/news"]);
  }
}
