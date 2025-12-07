import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { News } from './news';
import { UUID } from 'crypto';
import { ApiConfigService } from './api-config.service';

@Injectable({
  providedIn: 'root'
})
export class NewsServiceService {

  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService
  ) { }

  getAll(): Observable<News[]> {
    console.log("NewsService: getAll");
    return this.http.get<News[]>(this.apiConfig.getApiUrl('/news'));
  }

  getNews(newsId: string): Observable<News> {
    console.log("NewsService: getNews");
    return this.http.get<News>(this.apiConfig.getApiUrl(`/news/${newsId}`));
  }

  saveNews(id: UUID, news:News): Observable<News>{
    return this.http.post<News>(this.apiConfig.getApiUrl(`/news/${id}`), news);
  }

  updateNews(newsId: string, news:News): Observable<News>{
    return this.http.put<News>(this.apiConfig.getApiUrl(`/news/${newsId}`), news);
  }

  deleteNews(newsId: UUID): Observable<any> {
    return this.http.delete<any>(this.apiConfig.getApiUrl(`/news/${newsId}`));
  }
}
