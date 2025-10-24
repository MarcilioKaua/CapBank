import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatChip, MatChipOption } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { Transaction } from '../../shared/models/transaction.model';
import { QuickAction } from '../../shared/models/sidebar.model';

@Component({
  selector: 'app-dashboard',
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatChip,
    MatMenuModule,
    MatChipOption,
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  balance = signal(12547.89);
  balanceVisible = signal(false);

  formattedBalance = computed(() => {
    return this.balance().toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  });

  recentTransactions = signal<Transaction[]>([
    {
      id: '1',
      type: 'deposit',
      description: 'Depósito PIX',
      amount: 1250.0,
      date: 'Hoje, 14:30',
      icon: 'arrow_downward',
      iconColor: '#4caf50'
    },
    {
      id: '2',
      type: 'withdrawal',
      description: 'Supermercado ABC',
      amount: 89.5,
      date: 'Ontem, 18:45',
      icon: 'shopping_cart',
      iconColor: '#f44336'
    },
    {
      id: '3',
      type: 'transfer',
      description: 'Transferência para Maria',
      amount: 300.0,
      date: 'Ontem, 16:20',
      icon: 'send',
      iconColor: '#2196f3'
    },
    {
      id: '4',
      type: 'withdrawal',
      description: 'Conta de Luz',
      amount: 145.3,
      date: '22/10, 10:15',
      icon: 'flash_on',
      iconColor: '#ff9800'
    },
    {
      id: '5',
      type: 'deposit',
      description: 'Salário - Empresa XYZ',
      amount: 4500.0,
      date: '20/10, 08:00',
      icon: 'account_balance_wallet',
      iconColor: '#4caf50',
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
}
