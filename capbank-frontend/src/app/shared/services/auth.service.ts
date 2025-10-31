import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, finalize, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface LoginDTO {
  cpf: string;
  password: string;
}

interface UserLoginResponse {
  user: { id: string };
  token: { accessToken: string };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private router = inject(Router);
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl + '/api/user';

  public isLoading: WritableSignal<boolean> = signal(false);
  private token = signal<string | null>(null);

  login(data: LoginDTO): Observable<UserLoginResponse> {
    this.isLoading.set(true);
    return this.http.post<UserLoginResponse>(`${this.baseUrl}/validate`, data).pipe(
      tap((response) => {
        localStorage.setItem('auth-token', response.token.accessToken);
        localStorage.setItem('user-id', response.user.id);
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
    return this.token() || localStorage.getItem('auth-token');
  }

  logout() {
    this.token.set(null);
    localStorage.removeItem('auth-token');
    localStorage.removeItem('user-id');
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
