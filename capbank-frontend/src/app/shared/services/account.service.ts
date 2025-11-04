import { Injectable, inject, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, finalize, map, tap, delay } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

export interface UserDTO {
  fullName: string,
  cpf: string,
  email:string,
  accountType: string,
  password: string,
  confirmPassword: string
}

export interface ConfirmationDTO {
  verificationCode: string; 
  userId?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private http: HttpClient = inject(HttpClient);

  private baseUrl = environment.apiUrl;

  isLoading: WritableSignal<boolean> = signal(false);
  lastError: WritableSignal<string | null> = signal(null);
  lastMessage: WritableSignal<string | null> = signal(null);

  createdUserId: WritableSignal<string | null> = signal(null);

  constructor() {}

  createAccount(payload: UserDTO): Observable<any> {
    this.isLoading.set(true);
    this.lastError.set(null);

    const url = `${this.baseUrl}/api/user/register`;

    return this.http.post(url, payload).pipe(
      tap((res: any) => {
        if (res?.userId) this.createdUserId.set(res.userId);
        this.lastMessage.set('Conta criada (iniciada). Verifique o email.');
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
   * Envia (ou reenvia) código de verificação para o email do usuário.
   * Pode ser usada tanto para envio inicial quanto para "reenviar".
   */
  sendVerificationCode(email: string): Observable<any> {
    this.isLoading.set(true);
    this.lastError.set(null);

    const url = `${this.baseUrl}/accounts/send-verification`;

    return this.http.post(url, { email }).pipe(
      tap(() => this.lastMessage.set('Código de verificação enviado.')),
      catchError(err => {
        const msg = this.extractErrorMessage(err);
        this.lastError.set(msg);
        return throwError(() => err);
      }),
      finalize(() => this.isLoading.set(false))
    );
  }

  /**
   * Verifica o código enviado ao email.
   * Retorna Observable com sucesso/erro.
   */
  verifyCode(confirm: ConfirmationDTO): Observable<any> {
    this.isLoading.set(true);
    this.lastError.set(null);

    const url = `${this.baseUrl}/accounts/verify-code`;

    const payload = {
      code: confirm.verificationCode,
      userId: confirm.userId ?? this.createdUserId()
    };

    return this.http.post(url, payload).pipe(
      tap((res: any) => this.lastMessage.set('Conta verificada com sucesso.')),
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
