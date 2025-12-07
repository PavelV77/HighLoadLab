import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Like } from './like';
import { UUID } from 'crypto';
import { ApiConfigService } from './api-config.service';

@Injectable({
  providedIn: 'root'
})
export class LikeServiceService {

  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService
  ) { }

  addLike(dto: Like): Observable<Like> {
    console.log("LikeService: addLike ", dto);
    return this.http.post<Like>(this.apiConfig.getApiUrl('/news/activities'), dto)
  }

  deleteLike(likeId: UUID): Observable<any> {
    return this.http.delete<any>(this.apiConfig.getApiUrl(`/news/activities/${likeId}`))
  }
}
