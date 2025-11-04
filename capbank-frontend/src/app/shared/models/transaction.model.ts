import { TransactionType } from "./transaction-type.enum";
import { TransactionStatus } from "./transaction-status.enum";
import { TransactionHistory } from "./transaction-history.model";

// ============================================
// REQUEST DTOs
// ============================================

export interface DepositRequest {
  target_account_id: string;
  amount: number;
  description?: string;
}

export interface WithdrawalRequest {
  source_account_id: string;
  amount: number;
  description?: string;
}

export interface TransferRequest {
  source_account_id: string;
  target_account_id: string;
  amount: number;
  description?: string;
}

export interface UpdateTransactionStatusRequest {
  status: TransactionStatus;
  reason?: string;
}

// ============================================
// RESPONSE DTOs
// ============================================

export interface TransactionResponse {
  id: string;
  source_account_id?: string;
  target_account_id?: string;
  transaction_type: TransactionType;
  amount: number;
  description?: string;
  status: TransactionStatus;
  transaction_date: string;
  icon: string;
  iconColor: string;
}

export interface TransactionResultResponse {
  transaction: TransactionResponse;
  message: string;
  notificationSent: boolean;
}

export interface TransactionPageResponse {
  content: TransactionResponse[];
  page_number: number;
  page_size: number;
  total_elements: number;
  total_pages: number;
  first: boolean;
  last: boolean;
}

// ============================================
// QUERY PARAMS INTERFACES
// ============================================

export interface TransactionQueryParams {
  transactionType?: TransactionType;
  transactionStatus?: TransactionStatus;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

export interface TransactionHistoryQueryParams {
  transactionType?: TransactionType;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

// ============================================
// DISPLAY MODELS (Para UI)
// ============================================

export interface TransactionDisplay extends TransactionHistory {
  icon: string;
  iconColor: string;
  formattedAmount: string;
  formattedDate: string;
}

// ============================================
// BACKWARD COMPATIBILITY
// ============================================

