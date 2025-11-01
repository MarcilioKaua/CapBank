import { Injectable, inject, signal, WritableSignal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, finalize, map, tap, delay, switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { BankAccountResponse } from '../models/bank-account.model';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class BankAccountService {
  private http: HttpClient = inject(HttpClient);

  private baseUrl = environment.apiUrl;
  private cookieService = inject(CookieService);

  getBankAccountIdByNumber(accountNumber: string): Observable<string> {
    const token = this.cookieService.get('auth-token');
    const headers = new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json',
    });
    return this.http
      .get<BankAccountResponse>(`${this.baseUrl}/api/bankaccount/number/${accountNumber}`, {
        headers: headers,
      })
      .pipe(
        switchMap((accountResponse) => {
          return new Observable<string>((observer) => {
            observer.next(accountResponse.id);
            observer.complete();
          });
        })
      );
    }

}