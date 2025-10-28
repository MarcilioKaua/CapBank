import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { catchError, finalize, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface LoginDTO {
  cpf: string; 
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http: HttpClient = inject(HttpClient);
  private baseUrl = environment.apiUrl;
    
  isLoading: WritableSignal<boolean> = signal(false);
  lastError: WritableSignal<string | null> = signal(null);
  lastMessage: WritableSignal<string | null> = signal(null);


  constructor() {}
  
    login(payload: LoginDTO): Observable<any> {
      this.isLoading.set(true);
      this.lastError.set(null);
  
      const url = `${this.baseUrl}/api/auth/login`;
  
      return this.http.post(url, payload).pipe(
        tap((res: any) => {
          //if (res?.userId) this.createdUserId.set(res.userId);
          //obter token
          this.lastMessage.set('Login realizado com sucesso.');
        }),
        catchError(err => {
          const msg = this.extractErrorMessage(err);
          this.lastError.set(msg);
          return throwError(() => err);
        }),
        finalize(() => this.isLoading.set(false))
      );
    }

    
  /**
   * Helper: extrai mensagem de erro de um HttpErrorResponse (simplificado)
   */
  private extractErrorMessage(err: any): string {
    try {
      if (!err) return 'Erro desconhecido';
      // estruturas comuns: err.error.message || err.message || statusText
      return err?.error?.message || err?.message || `Erro: ${err?.status || '??'}`;
    } catch {
      return 'Erro desconhecido';
    }
  }

}