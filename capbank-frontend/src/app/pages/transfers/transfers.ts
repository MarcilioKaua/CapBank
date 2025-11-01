import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Sidebar } from '../../components/sidebar/sidebar';
import { CurrencyPipe } from '@angular/common';
import { CustomInputComponent } from '../../components/custom-input/custom-input';
import { TransactionService } from '../../shared/services/transaction.service';
import { TransferRequest } from '../../shared/models/transaction.model';
import { ToastComponent } from 'src/app/components/toast/toast.component';
import { ToastService } from 'src/app/shared/services/toast.service';
import { AccountMaskDirective } from 'src/app/shared/directives/account-mask.directive ';
import { BankAccountService } from 'src/app/shared/services/bank-account.service';

@Component({
  selector: 'app-transfers',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatToolbarModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    Sidebar,
    CurrencyPipe,
    CustomInputComponent,
    AccountMaskDirective
],
  templateUrl: './transfers.html',
  styleUrl: './transfers.css'
})
export class Transfers implements OnInit {
  isMobile = signal(window.innerWidth < 768);
  isLoading = signal(false);
  transferForm!: FormGroup;

  toast = inject(ToastService);
  bankAccountService = inject(BankAccountService);

  // Dados da conta (por enquanto mockados - pode ser substituído por dados do backend)
  availableBalance = signal(0);
  accountNumber = signal('');
  agencyNumber = signal('');

  balanceVisible = signal(false);

  sourceAccountId = signal<string>('');

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private transactionService: TransactionService
  ) {
    this.createTransferForm();
  }

  ngOnInit(): void {
    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
    this.loadAccountData();
  }

  private checkScreenSize(): void {
    this.isMobile.set(window.innerWidth < 768);
  }

  private createTransferForm(): void {
    this.transferForm = this.fb.group({
      destinationAccount: ['', [Validators.required]], //, Validators.pattern(/^\d{4,5}-\d{3}$/)
      amount: ['', [Validators.required, Validators.min(0.01)]],
      description: ['']
    });
  }

  /**
   * Método para carregar dados da conta do usuário logado
   * TODO: Implementar quando houver endpoint para buscar dados da conta
   */
  private loadAccountData(): void {
    this.transactionService.getBankAccount().subscribe({
      next: (account) => {
        this.sourceAccountId.set(account.id);
        this.accountNumber.set(account.accountNumber);
        this.agencyNumber.set(account.agency);
        this.availableBalance.set(account.balance);
      },
      error: (error) => {
        console.error('Erro ao buscar conta bancária:', error);
      },
    });
  }


  /**
   * Processa a transferência
   */
  onTransfer(): void {
    if (!this.transferForm.valid) {
      this.transferForm.markAllAsTouched();
      return;
    }

    const formData = this.transferForm.value;
    const amount = parseFloat(formData.amount);

    // Validação de saldo
    if (amount > this.availableBalance()) {
      this.toast.showError('Saldo insuficiente para esta transferência');
      return;
    }

    // Validação: não pode transferir para a própria conta
    if (formData.destinationAccount === this.accountNumber) {
      this.toast.showError('Não é possível transferir para a mesma conta');
      return;
    }

    this.bankAccountService.getBankAccountIdByNumber(formData.destinationAccount.replace('-', ''))
      .subscribe({
        next: (id) => {
          const transferData: TransferRequest = {
            source_account_id: this.sourceAccountId(),
            target_account_id: id,
            amount: amount,
            description: formData.description || `Transferência para conta ${formData.destinationAccount}`
          };

          // Executa a transferência
          this.performTransfer(transferData);
        },
        error: (err) => console.error('Erro ao buscar conta:', err)
      });
    
  }

  /**
   * Executa a transferência via serviço
   */
  private performTransfer(transferData: TransferRequest): void {
    this.isLoading.set(true);

    this.transactionService.createTransfer(transferData).subscribe({
      next: (result) => {
        this.isLoading.set(false);

        // Atualiza o saldo disponível (subtraindo o valor transferido)
        this.availableBalance.set(this.availableBalance() - transferData.amount);

        // Mostra mensagem de sucesso
        const message = result.message || 'Transferência realizada com sucesso!';
        this.toast.showSuccess(message);

        // Exibe informações adicionais se disponíveis
        if (result.notificationSent) {
          console.log('Notificação enviada com sucesso');
        }

        // Limpa o formulário
        this.transferForm.reset();

        // Log para debug
        console.log('Transferência concluída:', result);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.isLoading.set(false);
        const errorMessage = error.message || 'Erro ao processar transferência';
        this.toast.showError(errorMessage);
        console.error('Erro na transferência:', error);
      }
    });
  }

  /**
   * Navega de volta para o dashboard
   */
  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  /**
   * Formata valor para moeda brasileira
   */
  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  // ============================================
  // MÉTODOS DE VALIDAÇÃO E MENSAGENS DE ERRO
  // ============================================

  /**
   * Retorna mensagem de erro para o campo de conta destino
   */
  getDestinationAccountError(): string {
    const control = this.transferForm.get('destinationAccount');
    if (control?.hasError('required')) {
      return 'Conta destino é obrigatória';
    }
    // if (control?.hasError('pattern')) {
    //   return 'Formato inválido (ex: 12345-678)';
    // }
    return '';
  }

  /**
   * Retorna mensagem de erro para o campo de valor
   */
  getAmountError(): string {
    const control = this.transferForm.get('amount');
    if (control?.hasError('required')) {
      return 'Valor é obrigatório';
    }
    if (control?.hasError('min')) {
      return 'Valor deve ser maior que R$ 0,00';
    }
    return '';
  }
}
