export interface Account {
  id: string;
  accountNumber: string;
  agency: string;
  balance: number;
  accountType: 'checking' | 'savings';
  owner: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  cpf: string;
  phone: string;
}