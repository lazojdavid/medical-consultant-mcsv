import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError, timeout } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LoginCredentials } from '../models/login-credentials.model';
import { LoginResponse } from '../models/login-response.model';
import { SesionService } from './sesion-service';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private readonly API_URL = environment.apiUrl;
  private readonly LOGIN_URL = `${this.API_URL}/auth/login`;

  constructor(
    private readonly http: HttpClient,
    private readonly sesionService: SesionService,
  ) {}

  login(creds: LoginCredentials): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.LOGIN_URL, creds).pipe(
      timeout(5000),
      map((response: LoginResponse) => {
        if (response.accessToken) {
          this.sesionService.saveToken(response.accessToken);
        }

        return response;
      }),
      catchError((error: HttpErrorResponse) => {
        const message = error.error?.message ?? 'Error de autenticación';
        return throwError(() => new Error(message));
      }),
    );
  }
}
