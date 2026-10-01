import { Injectable } from '@angular/core';

const TOKEN_KEY = 'mcsv_access_token';
const REFRESH_TOKEN_KEY = 'mcsv_refresh_token';

@Injectable({
  providedIn: 'root',
})
export class SesionService {
  saveToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  saveRefreshToken(token: string): void {
    localStorage.setItem(REFRESH_TOKEN_KEY, token);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null && this.getToken() !== '';
  }
}
