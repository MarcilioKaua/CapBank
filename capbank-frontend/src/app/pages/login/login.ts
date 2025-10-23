import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';

@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatToolbarModule,
    MatMenuModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login implements OnInit {
  isMobile = signal(window.innerWidth < 768);
  hidePassword = signal(true);
  loginForm!: FormGroup;

  cardPlans = [
    {
      name: 'Classic',
      subtitle: 'Sem anuidade para sempre',
      color: '#2d3748',
      benefits: [
        'Cashback em compras',
        'Programa de pontos',
        'Proteção contra fraudes'
      ]
    },
    {
      name: 'Gold',
      subtitle: 'Benefícios exclusivos',
      color: '#d69e2e',
      popular: true,
      benefits: [
        '5% cashback em compras',
        'Acesso a salas VIP',
        'Seguro viagem incluído'
      ]
    },
    {
      name: 'Platinum',
      subtitle: 'O máximo em benefícios',
      color: '#805ad5',
      benefits: [
        '10% cashback premium',
        'Concierge 24h',
        'Milhas sem limite'
      ]
    }
  ];

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.createLoginForm();
  }

  ngOnInit(): void {
    this.checkScreenSize();
    window.addEventListener('resize', () => this.checkScreenSize());
  }

  private checkScreenSize(): void {
    this.isMobile.set(window.innerWidth < 768);
  }

  private createLoginForm(): void {
    this.loginForm = this.fb.group({
      cpf: ['', [Validators.required, Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/)]],
      password: ['', [Validators.required, Validators.minLength(4)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      console.log('Login attempt:', this.loginForm.value);
      // Simulate login success
      this.router.navigate(['/dashboard']);
    } else {
      this.loginForm.markAllAsTouched();
    }
  }

  togglePasswordVisibility(): void {
    this.hidePassword.update(hide => !hide);
  }

  forgotPassword(): void {
    console.log('Forgot password clicked');
    // Navigate to forgot password page
  }

  goToCreateAccount(): void {
    this.router.navigate(['/create-account']);
  }

  downloadApp(): void {
    console.log('Download app clicked');
  }

  learnMore(): void {
    console.log('Learn more clicked');
  }

  requestCard(cardType: string): void {
    console.log('Request card:', cardType);
  }
}
