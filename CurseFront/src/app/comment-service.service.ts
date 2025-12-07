import { HttpClient, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CommentDto } from './comment-dto';
import { ApiConfigService } from './api-config.service';

@Injectable({
  providedIn: 'root'
})
export class CommentServiceService {

  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService
  ) { }

  getAllForNews(newsId: String): Observable<CommentDto[]> {
    return this.http.get<CommentDto[]>(this.apiConfig.getApiUrl(`/news/${newsId}/comments`));
  }

  getComment(commentId: string): Observable<CommentDto> {
    return this.http.get<CommentDto>(this.apiConfig.getApiUrl(`/news/comments/${commentId}`));
  }

  saveComment(newsId: string, comment: CommentDto): Observable<CommentDto> {
    return this.http.post<CommentDto>(this.apiConfig.getApiUrl(`/news/${newsId}/comments`), comment);
  }

  updateComment(commentId: string, comment: CommentDto): Observable<CommentDto> {
    return this.http.put<CommentDto>(this.apiConfig.getApiUrl(`/news/comments/${commentId}`), comment);
  }

  deleteComment(commentId: string): Observable<any> {
    return this.http.delete<any>(this.apiConfig.getApiUrl(`/news/comments/${commentId}`));
  }
}
