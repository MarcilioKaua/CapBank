import { TransactionType } from "./transaction-type.enum";
import { TransactionStatus } from "./transaction-status.enum";

export interface TransactionHistory {
  id: string;
  account_id: string;
  transaction_id: string;
  description?: string;
  transaction_amount: number;
  balance_before: number;
  balance_after: number;
  transaction_type: TransactionType;
  status: TransactionStatus;
  record_date: string;
  icon: string;
  iconColor: string;
}


// ============================================
// TRANSACTION HISTORY DTOs
// ============================================

export interface TransactionHistory {
  id: string;
  account_id: string;
  transaction_id: string;
  balance_before: number;
  balance_after: number;
  transaction_amount: number;
  transaction_type: TransactionType;
  status: TransactionStatus;
  description?: string;
  record_date: string;
}

export interface TransactionHistoryPageResponse {
  content: TransactionHistory[];
  page_number: number;
  page_size: number;
  total_elements: number;
  total_pages: number;
  first: boolean;
  last: boolean;
}