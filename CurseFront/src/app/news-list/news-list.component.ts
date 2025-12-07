import { Component } from '@angular/core';
import { News } from '../news';
import { NewsServiceService } from '../news-service.service';
import { Like } from '../like';
import { UUID } from 'crypto';
import { LikeServiceService } from '../like-service.service';
import { switchMap } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-news-list',
  templateUrl: './news-list.component.html',
  styleUrl: './news-list.component.css'
})
export class NewsListComponent {
  constructor(private newsService: NewsServiceService, private likeService: LikeServiceService, private router: Router) { }
  newsList: News[] = [];
  isLike: boolean = false;
  ngOnInit(): void {
    this.newsService.getAll().subscribe(data => { this.newsList = data })
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
