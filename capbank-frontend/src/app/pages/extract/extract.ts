import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormControl, FormGroup } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatFormField, MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Transaction } from '../../shared/models/transaction.model';

interface TransactionGroup {
  date: string;
  totalPositive: number;
  totalNegative: number;
  transactions: Transaction[];
}

@Component({
  selector: 'app-extract',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatSelectModule,
    MatInputModule,
    MatFormFieldModule,
    MatTableModule,
    MatPaginatorModule,
    MatChipsModule,
    MatToolbarModule,
    MatIconModule,
  ],
  templateUrl: './extract.html',
  styleUrl: './extract.css',
})
export class Extract implements OnInit {
  filterForm = new FormGroup({
    period: new FormControl('7days'),
    type: new FormControl('all'),
    search: new FormControl(''),
  });

  isMobile = signal(window.innerWidth < 768);
  selectedPeriod = signal('7days');

  periodOptions = [
    { value: '7days', label: 'Últimos 7 dias' },
    { value: '30days', label: '30 dias' },
    { value: 'custom', label: 'Customizar' },
  ];

  typeOptions = [
    { value: 'all', label: 'Todas' },
    { value: 'deposit', label: 'Depósitos' },
    { value: 'withdrawal', label: 'Saques' },
    { value: 'transfer', label: 'Transferências' },
  ];

  displayedColumns = ['time', 'description', 'category', 'value', 'balance', 'actions'];

  allTransactions = signal<Transaction[]>([
    {
      id: '1',
      type: 'deposit',
      description: 'Depósito PIX - João Santos',
      amount: 1250.0,
      date: '2024-10-21T14:30:00',
      icon: 'arrow_downward',
      iconColor: '#4caf50',
    },
    {
      id: '2',
      type: 'transfer',
      description: 'Transferência PIX - Maria Silva',
      amount: 350.0,
      date: '2024-10-21T12:15:00',
      icon: 'send',
      iconColor: '#f44336',
    },
    {
      id: '3',
      type: 'withdrawal',
      description: 'Compra no Cartão - Supermercado ABC',
      amount: 127.45,
      date: '2024-10-21T09:45:00',
      icon: 'shopping_cart',
      iconColor: '#f44336',
    },
    {
      id: '4',
      type: 'deposit',
      description: 'Salário - Empresa XYZ Ltda',
      amount: 4500.0,
      date: '2024-10-20T18:20:00',
      icon: 'account_balance_wallet',
      iconColor: '#4caf50',
    },
    {
      id: '5',
      type: 'transfer',
      description: 'Transferência - Para Maria Silva',
      amount: 200.0,
      date: '2024-10-20T19:45:00',
      icon: 'swap_horiz',
      iconColor: '#f44336',
    },
    {
      id: '6',
      type: 'withdrawal',
      description: 'Combustível - Posto Shell',
      amount: 140.8,
      date: '2024-10-20T16:22:00',
      icon: 'local_gas_station',
      iconColor: '#f44336',
    },
    {
      id: '7',
      type: 'deposit',
      description: 'Salário - Empresa XYZ Ltda',
      amount: 3200.0,
      date: '2024-10-19T09:00:00',
      icon: 'account_balance_wallet',
      iconColor: '#4caf50',
    },
  ]);

  filteredTransactions = computed(() => {
    const search = this.filterForm.get('search')?.value?.toLowerCase() || '';
    const type = this.filterForm.get('type')?.value || 'all';

    return this.allTransactions().filter((transaction) => {
      const matchesSearch = transaction.description.toLowerCase().includes(search);
      const matchesType = type === 'all' || transaction.type === type;
      return matchesSearch && matchesType;
    });
  });

  groupedTransactions = computed(() => {
    const transactions = this.filteredTransactions();
    const groups: { [key: string]: TransactionGroup } = {};

    transactions.forEach((transaction) => {
      const date = new Date(transaction.date);
      const dateKey = date.toLocaleDateString('pt-BR');
      const displayDate = this.formatDateGroup(date);

      if (!groups[dateKey]) {
        groups[dateKey] = {
          date: displayDate,
          totalPositive: 0,
          totalNegative: 0,
          transactions: [],
        };
      }

      groups[dateKey].transactions.push({
        ...transaction,
        balance: this.calculateBalance(transaction.id),
      });

      if (transaction.type === 'deposit') {
        groups[dateKey].totalPositive += transaction.amount;
      } else {
        groups[dateKey].totalNegative += transaction.amount;
      }
    });

    return Object.values(groups).sort(
      (a, b) =>
        new Date(b.transactions[0].date).getTime() - new Date(a.transactions[0].date).getTime()
    );
  });

  ngOnInit(): void {
    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
  }

  private checkScreenSize(): void {
    this.isMobile.set(window.innerWidth < 768);
  }

  private formatDateGroup(date: Date): string {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Hoje, 21 Out';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Ontem, 20 Out';
    } else {
      return date.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' });
    }
  }

  private calculateBalance(transactionId: string): number {
    // Simulate balance calculation
    const baseBalance = 5000;
    const randomFactor = parseInt(transactionId) * 100;
    return baseBalance + randomFactor;
  }

  selectPeriod(period: string): void {
    this.selectedPeriod.set(period);
    this.filterForm.patchValue({ period });
  }

  formatAmount(amount: number, type: string): string {
    const formattedAmount = amount.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

    return type === 'deposit' ? `+R$ ${formattedAmount}` : `-R$ ${formattedAmount}`;
  }

  formatBalance(balance: number): string {
    return `R$ ${balance.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  }

  formatTime(dateString: string): string {
    return new Date(dateString).toLocaleTimeString('pt-BR', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  getTransactionCategory(transaction: Transaction): string {
    switch (transaction.type) {
      case 'deposit':
        return 'Depósito';
      case 'withdrawal':
        return transaction.description.includes('Cartão') ? 'Compra' : 'Saque';
      case 'transfer':
        return 'Transferência';
      default:
        return 'Outros';
    }
  }

  numberOfTransactions(group: TransactionGroup): string {
    const count = group.transactions.length;
    return `${count} ${count !== 1 ? 'transações' : 'transação'}`;
  }

  exportPDF(): void {
    console.log('Exportar PDF');
    // Implementar exportação PDF
  }

  viewDetails(transactionId: string): void {
    console.log('Ver detalhes da transação:', transactionId);
  }

  goBack(): void {
    window.history.back();
  }
}
