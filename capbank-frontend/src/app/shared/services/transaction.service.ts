import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import {
  DepositRequest,
  WithdrawalRequest,
  TransferRequest,
  TransactionResponse,
  TransactionResultResponse,
  TransactionPageResponse,
  TransactionHistoryPageResponse,
  UpdateTransactionStatusRequest,
  TransactionQueryParams,
  TransactionHistoryQueryParams
} from '../models/transaction.model';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  // URL base do microserviço de transações
  private readonly API_URL = 'http://localhost:8085/api/transaction';
  private readonly HISTORY_API_URL = 'http://localhost:8085/api/transaction-history';

  constructor(private http: HttpClient) {}

  // ============================================
  // OPERAÇÕES DE TRANSAÇÃO
  // ============================================

  /**
   * Realiza um depósito em uma conta
   * @param depositData Dados do depósito
   * @returns Observable com o resultado da transação
   */
  createDeposit(depositData: DepositRequest): Observable<TransactionResultResponse> {
    return this.http
      .post<TransactionResultResponse>(`${this.API_URL}/deposit`, depositData)
      
  }

  /**
   * Realiza um saque de uma conta
   * @param withdrawalData Dados do saque
   * @returns Observable com o resultado da transação
   */
  createWithdrawal(withdrawalData: WithdrawalRequest): Observable<TransactionResultResponse> {
    return this.http
      .post<TransactionResultResponse>(`${this.API_URL}/withdrawal`, withdrawalData)
     
  }

  /**
   * Realiza uma transferência entre contas
   * @param transferData Dados da transferência
   * @returns Observable com o resultado da transação
   */
  createTransfer(transferData: TransferRequest): Observable<TransactionResultResponse> {
    return this.http
      .post<TransactionResultResponse>(`${this.API_URL}/transfer`, transferData)
   
  }

  /**
   * Busca uma transação por ID
   * @param id ID da transação
   * @returns Observable com os dados da transação
   */
  getTransactionById(id: string): Observable<TransactionResponse> {
    return this.http
      .get<TransactionResponse>(`${this.API_URL}/${id}`)
   
  }


  /**
   * Atualiza o status de uma transação
   * @param id ID da transação
   * @param statusData Novo status e motivo (opcional)
   * @returns Observable com a transação atualizada
   */
  updateTransactionStatus(
    id: string,
    statusData: UpdateTransactionStatusRequest
  ): Observable<TransactionResponse> {
    return this.http
      .put<TransactionResponse>(`${this.API_URL}/${id}/status`, statusData)
   
  }

  // ============================================
  // HISTÓRICO DE TRANSAÇÕES
  // ============================================

  /**
   * Busca o histórico de transações de uma conta
   * @param accountId ID da conta
   * @param params Parâmetros de filtro e paginação
   * @returns Observable com o histórico paginado
   */

  /**
   * Busca um registro específico do histórico por ID
   * @param id ID do registro de histórico
   * @returns Observable com os dados do histórico
   */
  getTransactionHistoryById(id: string): Observable<TransactionHistoryPageResponse> {
    return this.http
      .get<TransactionHistoryPageResponse>(`${this.HISTORY_API_URL}/${id}`)
      
  }


}
