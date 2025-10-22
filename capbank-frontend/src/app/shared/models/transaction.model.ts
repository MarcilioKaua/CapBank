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
}

export interface QuickAction {
  id: string;
  title: string;
  subtitle: string;
  icon: string;
  color: string;
  route: string;
}

export interface MenuItem {
  id: string;
  label: string;
  icon: string;
  route: string;
}