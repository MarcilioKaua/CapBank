import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, finalize, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface LoginDTO {
  cpf: string; 
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private router = inject(Router);
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl + '/api/auth';

  public isLoading: WritableSignal<boolean> = signal(false);
  private token = signal<string | null>(null)

  login(data: LoginDTO): Observable<{ token: string }> {
    this.isLoading.set(true);
    return this.http.post<{ token: string }>(`${this.baseUrl}/login`, data).pipe(
      tap(response => {
        localStorage.setItem('auth-token', response.token);
        this.token.set(response.token)
      }),
      catchError(error => {
        return throwError(() => error);
      }),
      finalize(() => {
        this.isLoading.set(false);
      })
    );
  }

  getToken():string | null{
    return this.token() || localStorage.getItem('auth-token')
  }

  logout(){
    this.token.set(null)
    localStorage.removeItem('auth-token')
    this.router.navigate(['/login'])
  }
  
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

}