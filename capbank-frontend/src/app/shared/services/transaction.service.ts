import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, switchMap } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Transaction } from '../models/transaction.model';

export interface TransactionPageResponse {
  content: Transaction[];
  first: boolean;
  last: boolean;
  page_number: number;
  page_size: number;
  total_elements: number;
  total_pages: number;
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private http: HttpClient = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  constructor() {}

  getTransactions(): Observable<TransactionPageResponse> {
    const token = localStorage.getItem('auth-token');
    const userId = localStorage.getItem('user-id');

    const headers = new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json',
    });

    return this.http
      .get<{ id: string }>(`${this.baseUrl}/api/bankaccount/userId/${userId}`, { headers: headers })
      .pipe(
        switchMap((accountResponse) => {
          const transactionsUrl = `${this.baseUrl}/api/transaction-history/account/${accountResponse.id}`;
          return this.http.get<TransactionPageResponse>(transactionsUrl, { headers: headers });
        })
      );
  }
}
