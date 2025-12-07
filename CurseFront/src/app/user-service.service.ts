import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UUID } from 'crypto';
import { Observable } from 'rxjs';
import { User } from './user';
import { ApiConfigService } from './api-config.service';

@Injectable({
  providedIn: 'root'
})
export class UserServiceService {

  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService
  ) { }

  updateUser(id:string, user:User): Observable<User>{
    return this.http.put<User>(this.apiConfig.getApiUrl(`/users/${id}`), user);
  }
  deleteUser(id:UUID): Observable<any>{
    return this.http.delete<any>(this.apiConfig.getApiUrl(`/users/${id}`));
  }

  getAll(): Observable<User[]>{
    return this.http.get<User[]>(this.apiConfig.getApiUrl('/users'));
  }

  getUser(id:string): Observable<User>{
    return this.http.get<User>(this.apiConfig.getApiUrl(`/users/${id}`));
  }
}
