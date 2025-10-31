import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { Router, RouterModule } from '@angular/router';
import { TransactionService } from 'src/app/shared/services/transaction.service';
import { QuickAction } from '../../shared/models/sidebar.model';
import { TransactionResponse } from 'src/app/shared/models/transaction.model';

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
  balance = signal(0);
  balanceVisible = signal(false);
  accountNumber = signal('');
  agencyNumber = signal('');
  
  private router = inject(Router);
  recentTransactions = signal<TransactionResponse[]>([]);

  formattedBalance = computed(() => {
    return this.balance().toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
  });

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

  constructor(private transactionService: TransactionService) {}

  ngOnInit(): void {
    this.findBankAccount();
    this.findRecentTransactions();
  }

  findBankAccount(): void {
    this.transactionService.getBankAccount().subscribe({
      next: (account) => {
        this.accountNumber.set(account.accountNumber);
        this.agencyNumber.set(account.agency);
        this.balance.set(account.balance);
      },
      error: (error) => {
        console.error('Erro ao buscar conta bancária:', error);
      },
    });
  }

  findRecentTransactions(): void {
    this.transactionService.getRecentTransactions().subscribe({
      next: (transactions) => {
        const data = transactions.content;

        data.forEach((transaction) => {
          const { icon, iconColor } = this.getTransactionIcon(transaction.transaction_type);
          transaction.icon = icon;
          transaction.iconColor = iconColor;
        });

        this.recentTransactions.set(data);
      },
      error: (error) => {
        console.error('Erro ao buscar as transações recentes:', error);
      },
    });
  }

  getTransactionIcon = (type: string) => {
    switch (type.toLowerCase()) {
      case 'deposit':
        return { icon: 'arrow_downward', iconColor: '#4caf50' };
      case 'withdrawal':
        return { icon: 'arrow_upward', iconColor: '#f44336' };
      case 'transfer':
        return { icon: 'swap_horiz', iconColor: '#2196f3' };
      default:
        return { icon: 'help_outline', iconColor: '#9e9e9e' };
    }
  };

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

  goToTransfer(): void {
    this.router.navigate(['/transfers']);
  }

  goToExtract(): void {
    this.router.navigate(['/extract']);
  }
}
