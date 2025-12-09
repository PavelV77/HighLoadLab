import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  login: string = '';
  password: string = '';
  email: string = '';
  error: string = '';
  success: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.error = '';
    this.success = false;
    
    if (!this.login || !this.password) {
      this.error = 'Логин и пароль обязательны';
      return;
    }

    if (this.password.length < 3) {
      this.error = 'Пароль должен быть не менее 3 символов';
      return;
    }

    const registerData: any = {
      login: this.login,
      password: this.password
    };
    
    // Добавляем email только если он не пустой
    if (this.email && this.email.trim() !== '') {
      registerData.email = this.email.trim();
    }
    
    this.authService.register(registerData).subscribe({
      next: () => {
        this.success = true;
        setTimeout(() => {
          this.router.navigate(['/news']);
        }, 1500);
      },
      error: (err) => {
        if (err.status === 400) {
          // Пытаемся получить сообщение об ошибке из ответа
          const errorMessage = err.error?.message || err.error || 'Ошибка при регистрации';
          if (errorMessage.includes('already exists')) {
            this.error = 'Пользователь с таким логином или email уже существует';
          } else {
            this.error = errorMessage;
          }
        } else {
          this.error = 'Ошибка при регистрации. Попробуйте позже';
        }
        console.error('Register error:', err);
      }
    });
  }
}

