import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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
  //11111111-2222-3333-4444-555566667777
  //alterar para porta 8081 gateway
  url = 'http://localhost:8085/api/transaction-history/account';

  constructor(private http: HttpClient) {}

  getTransactions(accountId: string): Observable<TransactionPageResponse> {
    return this.http.get<TransactionPageResponse>(`${this.url}/${accountId}`);
  }
}
