import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, switchMap } from 'rxjs';
import { environment } from 'src/environments/environment';
import { TransactionHistory } from '../models/transaction-history.model';
import { Transaction } from '../models/transaction.model';

interface TransactionHistoryPageResponse {
  content: TransactionHistory[];
  first: boolean;
  last: boolean;
  page_number: number;
  page_size: number;
  total_elements: number;
  total_pages: number;
}

interface TransactionPageResponse {
  content: Transaction[];
  first: boolean;
  last: boolean;
  page_number: number;
  page_size: number;
  total_elements: number;
  total_pages: number;
}

interface BankAccountResponse {
  id: string;
  accountNumber: string;
  agency: string;
  balance: number;
  accountType: string;
  status: string;
  userId: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private http: HttpClient = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  constructor() {}

  getTransactionsHistory(): Observable<TransactionHistoryPageResponse> {
    const token = localStorage.getItem('auth-token');

    const headers = new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json',
    });

    return this.getBankAccount().pipe(
      switchMap((accountResponse) => {
        const transactionsUrl = `${this.baseUrl}/api/transaction-history/account/${accountResponse.id}`;
        return this.http.get<TransactionHistoryPageResponse>(transactionsUrl, {
          headers: headers,
        });
      })
    );
  }

  getRecentTransactions(): Observable<TransactionPageResponse> {
    const token = localStorage.getItem('auth-token');

    const headers = new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json',
    });

    return this.getBankAccount().pipe(
      switchMap((accountResponse) => {
        const transactionsUrl = `${this.baseUrl}/api/transaction/account/${accountResponse.id}`;
        return this.http.get<TransactionPageResponse>(transactionsUrl, {
          headers: headers,
        });
      })
    );
  }

  getBankAccount(): Observable<BankAccountResponse> {
    const token = localStorage.getItem('auth-token');
    const userId = localStorage.getItem('user-id');

    const headers = new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json',
    });

    return this.http.get<BankAccountResponse>(`${this.baseUrl}/api/bankaccount/userId/${userId}`, {
      headers: headers,
    });
  }
}
