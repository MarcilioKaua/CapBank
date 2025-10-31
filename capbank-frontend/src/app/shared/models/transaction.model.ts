export interface Transaction {
  id: string;
  source_account_id: string;
  target_account_id: string;
  transaction_type: 'deposit' | 'withdrawal' | 'transfer';
  amount: number;
  description: string;
  transaction_date: string;
  status: 'success' | 'pending' | 'failed';
  icon: string;
  iconColor: string;
}
