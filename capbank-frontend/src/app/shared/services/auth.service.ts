import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { catchError, finalize, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface LoginDTO {
  cpf: string;
  password: string;
}

interface UserLoginResponse {
  user: { id: string; fullName: string };
  token: { accessToken: string; expiresIn: number };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private router = inject(Router);
  private http = inject(HttpClient);
  private cookieService = inject(CookieService);
  private baseUrl = environment.apiUrl + '/api/user';

  public isLoading: WritableSignal<boolean> = signal(false);
  private token = signal<string | null>(null);

  login(data: LoginDTO): Observable<UserLoginResponse> {
    this.isLoading.set(true);
    return this.http.post<UserLoginResponse>(`${this.baseUrl}/validate`, data).pipe(
      tap((response) => {
        const token = response.token.accessToken;
        const userId = response.user.id;
        const userName = response.user.fullName;

        const expires = response.token.expiresIn / 86400;

        this.cookieService.set('auth-token', token, expires, '/');
        this.cookieService.set('user-id', userId, expires, '/');
        this.cookieService.set('user-name', userName, expires, '/');

        this.token.set(response.token.accessToken);
      }),
      catchError((error) => {
        return throwError(() => error);
      }),
      finalize(() => {
        this.isLoading.set(false);
      })
    );
  }

  getToken(): string | null {
    return this.token() || this.cookieService.get('auth-token');
  }

  logout() {
    this.token.set(null);
    this.cookieService.deleteAll('/');

    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
