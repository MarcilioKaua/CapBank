import { CommonModule } from '@angular/common';
import { Component, computed, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { TransactionService } from 'src/app/shared/services/transaction.service';
import { CustomInputComponent } from '../../components/custom-input/custom-input';
import { TransactionHistory } from 'src/app/shared/models/transaction-history.model';

interface TransactionGroup {
  date: string;
  totalPositive: number;
  totalNegative: number;
  transactions: TransactionHistory[];
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
    CustomInputComponent,
  ],
  templateUrl: './extract.html',
  styleUrl: './extract.css',
})
export class Extract implements OnInit {
  filterForm = new FormGroup({
    period: new FormControl('all'),
    type: new FormControl('all'),
    search: new FormControl(''),
    initialDate: new FormControl(`${new Date().getFullYear()}-01-01`),
    finalDate: new FormControl(new Date().toISOString().split('T')[0]),
  });

  isMobile = signal(window.innerWidth < 768);

  periodOptions = [
    { value: 'all', label: 'Todos' },
    { value: 'today', label: 'Hoje' },
    { value: '7days', label: 'Últimos 7 dias' },
    { value: '30days', label: '30 dias' },
  ];

  typeOptions = [
    { value: 'all', label: 'Todas' },
    { value: 'deposit', label: 'Depósitos' },
    { value: 'withdrawal', label: 'Saques' },
    { value: 'transfer', label: 'Transferências' },
  ];

  displayedColumns = ['time', 'description', 'category', 'value', 'balance'];

  allTransactions = signal<TransactionHistory[]>([]);

  currentPage = signal(1);
  pageSize = signal(10);

  constructor(private transactionService: TransactionService) {}

  filteredTransactions = computed(() => {
    const initialDateStr = this.filterForm.get('initialDate')?.value;
    const finalDateStr = this.filterForm.get('finalDate')?.value;

    const initialDate = initialDateStr ? new Date(initialDateStr + 'T00:00:00') : null;
    const finalDate = finalDateStr ? new Date(finalDateStr + 'T23:59:59') : null;

    const search = this.filterForm.get('search')?.value?.toLowerCase() || '';
    const type = this.filterForm.get('type')?.value || 'all';

    return this.allTransactions().filter((transaction) => {
      const transactionDate = new Date(transaction.record_date);

      const matchesSearch = transaction.description.toLowerCase().includes(search);
      const matchesType = type === 'all' || transaction.transaction_type.toLowerCase() === type;

      let matchesDate = true;

      if (initialDate) {
        matchesDate = matchesDate && transactionDate >= initialDate;
      }

      if (finalDate) {
        matchesDate = matchesDate && transactionDate <= finalDate;
      }

      return matchesSearch && matchesType && matchesDate;
    });
  });

  groupedTransactions = computed(() => {
    const transactions = this.paginatedTransactions();
    const groups: { [key: string]: TransactionGroup } = {};

    transactions.forEach((transaction) => {
      const date = new Date(transaction.record_date);
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
      });

      if (transaction.transaction_type.toLowerCase() === 'deposit') {
        groups[dateKey].totalPositive += transaction.transaction_amount;
      } else {
        groups[dateKey].totalNegative += transaction.transaction_amount;
      }
    });

    return Object.values(groups).sort(
      (a, b) =>
        new Date(b.transactions[0].record_date).getTime() -
        new Date(a.transactions[0].record_date).getTime()
    );
  });

  paginatedTransactions = computed(() => {
    const transactions = this.filteredTransactions();
    const page = this.currentPage();
    const size = this.pageSize();

    const startIndex = (page - 1) * size;
    const endIndex = startIndex + size;

    return transactions.slice(startIndex, endIndex);
  });

  get totalPages(): number {
    const total = this.filteredTransactions().length;
    return Math.ceil(total / this.pageSize());
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage.set(page);
    }
  }

  nextPage(): void {
    this.goToPage(this.currentPage() + 1);
  }

  previousPage(): void {
    this.goToPage(this.currentPage() - 1);
  }

  ngOnInit(): void {
    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
    this.findTransactions();
    this.setupPeriodChangeListener();
  }

  private checkScreenSize(): void {
    this.isMobile.set(window.innerWidth < 768);
  }

  private formatDateGroup(date: Date): string {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return `Hoje, ${today.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' })}`;
    } else if (date.toDateString() === yesterday.toDateString()) {
      return `Ontem, ${yesterday.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' })}`;
    } else {
      return date.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' });
    }
  }

  formatAmount(amount: number, type: string): string {
    const formattedAmount = amount.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

    return type.toLowerCase() === 'deposit' ? `+R$ ${formattedAmount}` : `-R$ ${formattedAmount}`;
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

  getTransactionCategory(transaction: TransactionHistory): string {
    switch (transaction.transaction_type.toLowerCase()) {
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

  findTransactions(): void {
    this.transactionService.getTransactionsHistory().subscribe({
      next: (transactions) => {
        const data = transactions.content;
        data.forEach((transaction) => {
          const { icon, iconColor } = this.getTransactionIcon(transaction.transaction_type);
          transaction.icon = icon;
          transaction.iconColor = iconColor;
        });

        this.allTransactions.set(data);
      },
      error: (error) => {
        console.error('Erro ao buscar transações:', error);
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

  setupPeriodChangeListener() {
    this.filterForm.get('period')?.valueChanges.subscribe((selectedPeriod) => {
      let newInitialDate = null;
      switch (selectedPeriod) {
        case 'all':
          newInitialDate = `2000-01-01`;
          break;
        case 'today':
          newInitialDate = this.getTodayDateString();
          break;
        case '7days':
          newInitialDate = this.getDateStringDaysAgo(7);
          break;
        case '30days':
          newInitialDate = this.getDateStringDaysAgo(30);
          break;
        default:
          newInitialDate = `${new Date().getFullYear()}-01-01`;
          break;
      }
      this.filterForm.get('initialDate')?.setValue(newInitialDate);
      this.filterForm.get('finalDate')?.setValue(this.getTodayDateString());
    });
  }

  getTodayDateString(): string {
    return new Date().toISOString().split('T')[0];
  }

  getDateStringDaysAgo(days: number): string {
    const date = new Date();
    date.setDate(date.getDate() - days);
    return date.toISOString().split('T')[0];
  }

  viewDetails(transactionId: string): void {
    console.log('Ver detalhes da transação:', transactionId);
  }

  goBack(): void {
    window.history.back();
  }
}
