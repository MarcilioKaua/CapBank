export interface Transaction {
  id: string;
  type: 'deposit' | 'withdrawal' | 'transfer';
  description: string;
  amount: number;
  date: string;
  icon: string;
  iconColor: string;
  sourceAccountId?: string;
  targetAccountId?: string;
  balance?: number;
}

