import { CommonModule } from '@angular/common';
import { Component, computed, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterModule } from '@angular/router';
import { QuickAction } from '../../shared/models/sidebar.model';
import { Transaction } from '../../shared/models/transaction.model';

@Component({
  selector: 'app-dashboard',
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatMenuModule,
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  balance = signal(12547.89);
  balanceVisible = signal(false);

  formattedBalance = computed(() => {
    return this.balance().toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
  });

  recentTransactions = signal<Transaction[]>([
    {
      id: '1a2b3c4d-5e6f-7001-89ab-cdef01234567',
      account_id: 'acc-98765',
      transaction_id: 'txn-5001',
      transaction_type: 'deposit',
      description: 'Depósito Salarial Mensal',
      transaction_amount: 500.0,
      balance_before: 2100.5,
      balance_after: 7600.5,
      record_date: '2025-10-25T09:30:00',
      status: 'completed',
      icon: 'payments',
      iconColor: 'green',
    },
    {
      id: '2b3c4d5e-6f70-8112-9abc-def012345678',
      account_id: 'acc-98765',
      transaction_id: 'txn-5002',
      transaction_type: 'withdrawal',
      description: 'Compra em Supermercado',
      transaction_amount: 150.75,
      balance_before: 7600.5,
      balance_after: 7449.75,
      record_date: '2025-10-26T14:15:20',
      status: 'completed',
      icon: 'shopping_cart',
      iconColor: 'red',
    },
    {
      id: '3c4d5e6f-7081-9223-abca-f01234567890',
      account_id: 'acc-98765',
      transaction_id: 'txn-5003',
      transaction_type: 'transfer',
      description: 'Transferência para Poupança',
      transaction_amount: 1000.0,
      balance_before: 7449.75,
      balance_after: 6449.75,
      record_date: '2025-10-27T18:05:45',
      status: 'completed',
      icon: 'swap_horiz',
      iconColor: 'blue',
    },
  ]);

  quickActions: QuickAction[] = [
    {
      id: 'transfer',
      title: 'Nova Transferência',
      subtitle: 'Envie dinheiro rapidamente',
      icon: 'send',
      color: '#ff9800',
      route: '/transfers',
    },
    {
      id: 'extract',
      title: 'Extrato Completo',
      subtitle: 'Veja todas as movimentações',
      icon: 'receipt_long',
      color: '#2196f3',
      route: '/extract',
    },
    {
      id: 'profile',
      title: 'Meu Perfil',
      subtitle: 'Gerencie suas informações',
      icon: 'person',
      color: '#4caf50',
      route: '/profile',
    },
  ];

  ngOnInit(): void {
    // Initialize component
  }

  toggleBalanceVisibility(): void {
    this.balanceVisible.update((visible) => !visible);
  }

  getAmountClass(type: string): string {
    return type === 'deposit'
      ? 'transaction-item__amount--positive'
      : 'transaction-item__amount--negative';
  }

  formatAmount(amount: number, type: string): string {
    const formattedAmount = amount.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

    return type === 'deposit' ? `+ R$ ${formattedAmount}` : `- R$ ${formattedAmount}`;
  }

  formatDateTime(dateString: string): string {
    return new Date(dateString).toLocaleDateString('pt-BR', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false,
    });
  }
}
