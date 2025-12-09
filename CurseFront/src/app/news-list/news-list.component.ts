import { Component, OnInit, OnDestroy } from '@angular/core';
import { News } from '../news';
import { NewsServiceService } from '../news-service.service';
import { Like } from '../like';
import { UUID } from 'crypto';
import { LikeServiceService } from '../like-service.service';
import { switchMap } from 'rxjs';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../auth.service';
import { filter, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-news-list',
  templateUrl: './news-list.component.html',
  styleUrl: './news-list.component.css'
})
export class NewsListComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  constructor(
    private newsService: NewsServiceService, 
    private likeService: LikeServiceService, 
    private router: Router,
    private authService: AuthService
  ) {
    console.log('NewsListComponent constructor called');
  }
  newsList: News[] = [];
  isLike: boolean = false;
  
  ngOnInit(): void {
    console.log('NewsListComponent ngOnInit called, current route:', this.router.url);
    // Загружаем данные при инициализации
    this.loadNews();
    
    // Подписываемся на события навигации, чтобы перезагружать данные при возврате на этот роут
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      filter(() => this.router.url === '/news'),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      console.log('Navigation to /news detected, reloading data');
      this.loadNews();
    });
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadNews(): void {
    console.log('Loading news, token:', this.authService.getToken() ? 'present' : 'missing');
    this.newsService.getAll().subscribe({
      next: (data) => { 
        console.log('News loaded successfully, count:', data.length);
        this.newsList = data;
      },
      error: (err) => {
        console.error('Error loading news:', err);
        // Не очищаем список при ошибке, чтобы не показывать пустую страницу
        // Interceptor обработает 401 и сделает редирект
      }
    });
  }


  like(newsId: UUID): void {
    let dto: Like = {
      id: "1cb23e39-ee49-46f5-8592-91c6ab32ef97",
      typeOfActivity: 1,
      userId: "1cb23e39-ee49-46f5-8592-91c6ab32ef97",
      newsId: newsId,
      insertAt: 0,
      updateAt: 0
    };
    this.likeService.addLike(dto).pipe(switchMap(() => this.newsService.getAll())).subscribe(data => this.newsList = data);
    this.isLike = true;
  }

  dislike(newsId: UUID): void {
    let dto: Like = {
      id: "1cb23e39-ee49-46f5-8592-91c6ab32ef97",
      typeOfActivity: 2,
      userId: "1cb23e39-ee49-46f5-8592-91c6ab32ef97",
      newsId: newsId,
      insertAt: 0,
      updateAt: 0
    };
    this.likeService.addLike(dto).pipe(switchMap(() => this.newsService.getAll())).subscribe(data => this.newsList = data);
  }

  edit(newsId: UUID): void {
    this.router.navigate(["news/" + newsId + "/edit"]);
  }

  delete(newsId: UUID): void {
    this.newsService.deleteNews(newsId).pipe(switchMap(() => this.newsService.getAll())).subscribe(data => this.newsList = data);
  }

  comment(newsId: UUID): void {
    this.router.navigate(["news/" + newsId + "/comments"]);
  }

}
