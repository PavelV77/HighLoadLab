import { Component, OnInit, OnDestroy } from '@angular/core';
import { User } from '../user';
import { UserServiceService } from '../user-service.service';
import { Router, NavigationEnd } from '@angular/router';
import { UUID } from 'crypto';
import { switchMap } from 'rxjs';
import { AuthService } from '../auth.service';
import { filter, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  userList: User[] = [];
  constructor(
    private userService: UserServiceService, 
    private router: Router,
    private authService: AuthService
  ) {
    console.log('UserListComponent constructor called');
  }

  ngOnInit(){
    console.log('UserListComponent ngOnInit called, current route:', this.router.url);
    // Загружаем данные при инициализации
    this.loadUsers();
    
    // Подписываемся на события навигации, чтобы перезагружать данные при возврате на этот роут
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      filter(() => this.router.url === '/users'),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      console.log('Navigation to /users detected, reloading data');
      this.loadUsers();
    });
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadUsers(): void {
    console.log('Loading users, token:', this.authService.getToken() ? 'present' : 'missing');
    this.userService.getAll().subscribe({
      next: (data) => { 
        console.log('Users loaded successfully, count:', data.length);
        this.userList = data;
      },
      error: (err) => {
        console.error('Error loading users:', err);
        // Не очищаем список при ошибке, чтобы не показывать пустую страницу
        // Interceptor обработает 401 и сделает редирект
      }
    });
  }
  update(id: UUID): void{
    this.router.navigate(["users/"+id+"/edit"]);
  }
  delete(id: UUID): void{
    this.userService.deleteUser(id).pipe(switchMap(()=>this.userService.getAll())).subscribe(data => this.userList = data);
  }
}
