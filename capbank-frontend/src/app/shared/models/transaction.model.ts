export interface Transaction {
  id: string;
  account_id: string;
  transaction_id: string;
  transaction_type: 'deposit' | 'withdrawal' | 'transfer';
  description: string;
  transaction_amount: number;
  balance_before: number;
  balance_after: number;
  record_date: string;
  status: string;
  icon: string;
  iconColor: string;
}
