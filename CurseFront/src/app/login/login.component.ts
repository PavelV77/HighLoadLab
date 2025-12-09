import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  login: string = '';
  password: string = '';
  error: string = '';
  infoMessage: string = '';
  returnUrl: string = '/news';

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Проверяем, был ли редирект из-за отсутствия авторизации
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/news';
    
    // Если пользователь уже авторизован, редиректим
    if (this.authService.isAuthenticated()) {
      this.router.navigate([this.returnUrl]);
    } else {
      // Показываем сообщение, если был редирект
      if (this.returnUrl !== '/news') {
        this.infoMessage = 'Для доступа к этой странице необходимо войти в систему';
      }
    }
  }

  onSubmit(): void {
    this.error = '';
    this.infoMessage = '';
    
    if (!this.login || !this.password) {
      this.error = 'Пожалуйста, заполните все поля';
      return;
    }

    this.authService.login({
      login: this.login,
      password: this.password
    }).subscribe({
      next: () => {
        // После успешного входа редиректим на страницу, с которой пришли, или на /news
        this.router.navigate([this.returnUrl]);
      },
      error: (err) => {
        this.error = 'Неверный логин или пароль';
        console.error('Login error:', err);
      }
    });
  }
}

