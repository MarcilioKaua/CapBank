import { Component, computed, HostListener, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatToolbarModule } from '@angular/material/toolbar';
import { CpfMaskDirective } from '../../shared/directives/cpf-mask.directive';
import { PhoneMaskDirective } from '../../shared/directives/phone-mask.directive';
import { CreateAccountService } from 'src/app/core/services/create-account.service';

@Component({
  selector: 'app-create-account',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MatTabsModule,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatSelectModule,
    MatCheckboxModule,
    MatToolbarModule,
    CpfMaskDirective,
    PhoneMaskDirective
  ],
  templateUrl: './create-account.html',
  styleUrls: ['./create-account.css']
})
export class CreateAccount implements OnInit {
  isMobile: WritableSignal<boolean> = signal(window.innerWidth < 768);
  currentStep: WritableSignal<number> = signal(0);

  personalDataForm!: FormGroup;
  accessForm!: FormGroup;
  confirmationForm!: FormGroup;

  steps = [
    { label: 'Dados Pessoais', icon: 'person' },
    { label: 'Acesso', icon: 'lock' },
    { label: 'Confirmação', icon: 'check_circle' }
  ];
  
  currentStepTitle = computed(() => this.steps[this.currentStep()].label);

  private fb = inject(FormBuilder);
  private createAccountService = inject(CreateAccountService);
  isLoading = this.createAccountService.isLoading;
  lastError = this.createAccountService.lastError;

  constructor() {
    this.createForms();
  }

  ngOnInit(): void {
    this.checkScreenSize();
  }
  
  @HostListener('window:resize')
  onResize(): void {
    this.isMobile.set(window.innerWidth < 768);
  }

  private checkScreenSize(): void {
    this.isMobile.set(window.innerWidth < 768);
  }

  private createForms(): void {
    this.personalDataForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      cpf: ['', [Validators.required, Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/)]],
      birthDate: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern(/^\(\d{2}\) \d{4,5}-\d{4}$/)]]
    });

    this.accessForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
      acceptTerms: [false, Validators.requiredTrue]
    }, { validators: this.passwordsMatchValidator });

    this.confirmationForm = this.fb.group({
      verificationCode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
    });
  }

  private passwordsMatchValidator(group: FormGroup) {
    const pw = group.get('password')?.value;
    const cpw = group.get('confirmPassword')?.value;
    return pw === cpw ? null : { passwordsMismatch: true };
  }

  nextStep(): void {
    if (this.currentStep() < this.steps.length - 1) {
      this.currentStep.update(step => step + 1);
    }
  }

  previousStep(): void {
    if (this.currentStep() > 0) {
      this.currentStep.update(step => step - 1);
    }
  }

  goBack(): void {
    if (this.currentStep() === 0) {
      window.history.back();
    } else {
      this.previousStep();
    }
  }

  onSubmitPersonalData(): void {
    if (this.personalDataForm.valid) {
      console.log('Personal data:', this.personalDataForm.value);
      this.nextStep();
    } else {
      this.personalDataForm.markAllAsTouched();
    }
  }
  
  private onlyDigits(value: string): string {
    return (value || '').toString().replace(/\D/g, '');
  }
  
/*
  onSubmitAccess(): void {
    if (this.accessForm.valid) {
      console.log('Access data:', this.accessForm.value);
      this.nextStep();
    } else {
      this.accessForm.markAllAsTouched();
    }
  }*/

  onSubmitAccess() {
    if (this.accessForm.valid) {
      const personal = this.personalDataForm.value;
      const access = this.accessForm.value;

      const payload = {        
        fullName: personal.fullName,
        cpf: this.onlyDigits(personal.cpf),
        email: access.email,
        accountType: "ZZZ",
        password: access.password,
        confirmPassword: access.confirmPassword
      }

      this.createAccountService.createAccount(payload).subscribe({
        next: res => {
          // caso backend retorne userId, ele já foi salvo no serviço
          this.nextStep();
          // opcional: chamar sendVerificationCode
          //this.createAccountService.sendVerificationCode(access.email).subscribe();
        },
        error: err => {
          // erro já definido em lastError signal; aqui podemos mostrar snackbar ou similar
          console.error(err);
        }
      });
    } else {
      this.accessForm.markAllAsTouched();
    }
  }

  onSubmitConfirmation(): void {
    if (this.confirmationForm.valid) {
      console.log('Confirmation:', this.confirmationForm.value);
      console.log('Account created successfully!');
    } else {
      this.confirmationForm.markAllAsTouched();
    }
  }

  resendCode(): void {
    console.log('Resending verification code...');
  }

  getCurrentStepTitle(): string {
    return this.currentStepTitle();
  }

  isStepCompleted(stepIndex: number): boolean {
    return stepIndex < this.currentStep();
  }
}
