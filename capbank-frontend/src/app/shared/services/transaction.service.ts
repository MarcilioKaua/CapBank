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
      .pipe(catchError(this.handleError));
  }

  /**
   * Realiza um saque de uma conta
   * @param withdrawalData Dados do saque
   * @returns Observable com o resultado da transação
   */
  createWithdrawal(withdrawalData: WithdrawalRequest): Observable<TransactionResultResponse> {
    return this.http
      .post<TransactionResultResponse>(`${this.API_URL}/withdrawal`, withdrawalData)
      .pipe(catchError(this.handleError));
  }

  /**
   * Realiza uma transferência entre contas
   * @param transferData Dados da transferência
   * @returns Observable com o resultado da transação
   */
  createTransfer(transferData: TransferRequest): Observable<TransactionResultResponse> {
    return this.http
      .post<TransactionResultResponse>(`${this.API_URL}/transfer`, transferData)
      .pipe(catchError(this.handleError));
  }

  /**
   * Busca uma transação por ID
   * @param id ID da transação
   * @returns Observable com os dados da transação
   */
  getTransactionById(id: string): Observable<TransactionResponse> {
    return this.http
      .get<TransactionResponse>(`${this.API_URL}/${id}`)
      .pipe(catchError(this.handleError));
  }

  /**
   * Busca transações de uma conta com filtros e paginação
   * @param accountId ID da conta
   * @param params Parâmetros de filtro e paginação
   * @returns Observable com a lista paginada de transações
   */
  getTransactionsByAccount(
    accountId: string,
    params?: TransactionQueryParams
  ): Observable<TransactionPageResponse> {
    let httpParams = new HttpParams();

    if (params) {
      if (params.transactionType) {
        httpParams = httpParams.set('transactionType', params.transactionType);
      }
      if (params.transactionStatus) {
        httpParams = httpParams.set('transactionStatus', params.transactionStatus);
      }
      if (params.startDate) {
        httpParams = httpParams.set('startDate', params.startDate);
      }
      if (params.endDate) {
        httpParams = httpParams.set('endDate', params.endDate);
      }
      if (params.page !== undefined) {
        httpParams = httpParams.set('page', params.page.toString());
      }
      if (params.size !== undefined) {
        httpParams = httpParams.set('size', params.size.toString());
      }
      if (params.sortBy) {
        httpParams = httpParams.set('sortBy', params.sortBy);
      }
      if (params.sortDirection) {
        httpParams = httpParams.set('sortDirection', params.sortDirection);
      }
    }

    return this.http
      .get<TransactionPageResponse>(`${this.API_URL}/account/${accountId}`, { params: httpParams })
      .pipe(catchError(this.handleError));
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
      .pipe(catchError(this.handleError));
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
  getTransactionHistory(
    accountId: string,
    params?: TransactionHistoryQueryParams
  ): Observable<TransactionHistoryPageResponse> {
    let httpParams = new HttpParams();

    if (params) {
      if (params.transactionType) {
        httpParams = httpParams.set('transactionType', params.transactionType);
      }
      if (params.startDate) {
        httpParams = httpParams.set('startDate', params.startDate);
      }
      if (params.endDate) {
        httpParams = httpParams.set('endDate', params.endDate);
      }
      if (params.page !== undefined) {
        httpParams = httpParams.set('page', params.page.toString());
      }
      if (params.size !== undefined) {
        httpParams = httpParams.set('size', params.size.toString());
      }
      if (params.sortBy) {
        httpParams = httpParams.set('sortBy', params.sortBy);
      }
      if (params.sortDirection) {
        httpParams = httpParams.set('sortDirection', params.sortDirection);
      }
    }

    return this.http
      .get<TransactionHistoryPageResponse>(`${this.HISTORY_API_URL}/account/${accountId}`, {
        params: httpParams,
      })
      .pipe(catchError(this.handleError));
  }

  /**
   * Busca um registro específico do histórico por ID
   * @param id ID do registro de histórico
   * @returns Observable com os dados do histórico
   */
  getTransactionHistoryById(id: string): Observable<TransactionHistoryPageResponse> {
    return this.http
      .get<TransactionHistoryPageResponse>(`${this.HISTORY_API_URL}/${id}`)
      .pipe(catchError(this.handleError));
  }

  // ============================================
  // BACKWARD COMPATIBILITY
  // ============================================

  /**
   * @deprecated Use getTransactionHistory instead
   * Mantido para compatibilidade com código existente
   */
  getTransactions(accountId: string): Observable<TransactionHistoryPageResponse> {
    return this.getTransactionHistory(accountId);
  }

  // ============================================
  // TRATAMENTO DE ERROS
  // ============================================

  /**
   * Trata erros HTTP e retorna mensagem apropriada
   * @param error Erro HTTP
   * @returns Observable com erro
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Erro ao processar a requisição';

    if (error.error instanceof ErrorEvent) {
      // Erro do lado do cliente
      errorMessage = `Erro: ${error.error.message}`;
    } else {
      // Erro do lado do servidor
      if (error.status === 400) {
        errorMessage = 'Dados inválidos. Verifique os campos e tente novamente.';
      } else if (error.status === 404) {
        errorMessage = 'Transação não encontrada.';
      } else if (error.status === 500) {
        errorMessage = 'Erro interno do servidor. Tente novamente mais tarde.';
      } else if (error.error?.message) {
        errorMessage = error.error.message;
      } else {
        errorMessage = `Erro ${error.status}: ${error.message}`;
      }
    }

    console.error('Erro na requisição:', error);
    return throwError(() => new Error(errorMessage));
  }
}
