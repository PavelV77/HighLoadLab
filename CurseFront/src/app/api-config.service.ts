import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiConfigService {
  private baseUrl: string = environment.apiUrl;

  constructor() {
    console.log('API Base URL:', this.baseUrl);
  }

  getBaseUrl(): string {
    return this.baseUrl;
  }

  getApiUrl(endpoint: string): string {
    return `${this.baseUrl}${endpoint.startsWith('/') ? endpoint : '/' + endpoint}`;
  }
}

