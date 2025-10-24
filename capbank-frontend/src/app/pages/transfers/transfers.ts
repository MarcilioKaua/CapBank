import { Component, OnInit, signal } from '@angular/core';
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
import { Sidebar } from '../../components/sidebar/sidebar';
import { CurrencyPipe } from '@angular/common';
import { DEFAULT_CURRENCY_CODE } from '@angular/core';
import { CustomInputComponent } from '../../components/custom-input/custom-input';

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
    Sidebar,
    CurrencyPipe,
    CustomInputComponent
  ],
  templateUrl: './transfers.html',
  styleUrl: './transfers.css'
})
export class Transfers implements OnInit {
  isMobile = signal(window.innerWidth < 768);
  transferForm!: FormGroup;
  availableBalance = 15847.50;
  accountNumber = '12345-6';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.createTransferForm();
  }

  ngOnInit(): void {
    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
  }

  private checkScreenSize(): void {
    this.isMobile.set(window.innerWidth < 768);
  }

  private createTransferForm(): void {
    this.transferForm = this.fb.group({
      destinationAccount: ['', [Validators.required, Validators.pattern(/^\d{4,5}-\d{1}$/)]],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      description: ['']
    });
  }

  onTransfer(): void {
    if (this.transferForm.valid) {
      const transferData = this.transferForm.value;
      const amount = parseFloat(transferData.amount);

      if (amount > this.availableBalance) {
        this.snackBar.open('Saldo insuficiente para esta transferência', 'Fechar', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        return;
      }

      console.log('Transfer data:', transferData);
      this.snackBar.open('Transferência realizada com sucesso!', 'Fechar', {
        duration: 3000,
        panelClass: ['success-snackbar']
      });

      this.transferForm.reset();
    } else {
      this.transferForm.markAllAsTouched();
    }
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  // Métodos para obter mensagens de erro
  getDestinationAccountError(): string {
    const control = this.transferForm.get('destinationAccount');
    if (control?.hasError('required')) {
      return 'Conta destino é obrigatória';
    }
    if (control?.hasError('pattern')) {
      return 'Formato inválido (ex: 12345-6)';
    }
    return '';
  }

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
