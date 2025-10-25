import { Component, computed, HostListener, inject, OnInit, signal, WritableSignal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router,RouterModule } from '@angular/router';
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
import { CreateAccountService } from 'src/app/shared/services/create-account.service';
import { CustomInputComponent } from 'src/app/components/custom-input/custom-input';
import { ToastService } from 'src/app/shared/services/toast.service';

@Component({
  selector: 'app-create-account',
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ReactiveFormsModule,
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
    PhoneMaskDirective,
    CustomInputComponent,
  ],
  templateUrl: './create-account.html',
  styleUrls: ['./create-account.css'],
})
export class CreateAccount implements OnInit {
  isMobile: WritableSignal<boolean> = signal(window.innerWidth < 768);
  currentStep: WritableSignal<number> = signal(0);

  personalDataForm!: FormGroup;
  accessForm!: FormGroup;

  steps = [
    { label: 'Dados Pessoais', icon: 'person' },
    { label: 'Acesso', icon: 'lock' },
  ];

  currentStepTitle = computed(() => this.steps[this.currentStep()].label);

  private fb = inject(FormBuilder);
  private router = inject(Router);
  private toast = inject(ToastService);
  
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
      phone: ['', [Validators.required, Validators.pattern(/^\(\d{2}\) \d{4,5}-\d{4}$/)]],
    });

    this.accessForm = this.fb.group(
      {
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', Validators.required],
        acceptTerms: [false, Validators.requiredTrue],
      },
      { validators: this.passwordsMatchValidator }
    );

  }

  private passwordsMatchValidator(group: FormGroup) {
    const pw = group.get('password')?.value;
    const cpw = group.get('confirmPassword')?.value;
    return pw === cpw ? null : { passwordsMismatch: true };
  }

  nextStep(): void {
    if (this.currentStep() < this.steps.length - 1) {
      this.currentStep.update((step) => step + 1);
    }
  }

  previousStep(): void {
    if (this.currentStep() > 0) {
      this.currentStep.update((step) => step - 1);
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
      this.nextStep();
    } else {
      this.personalDataForm.markAllAsTouched();
    }
  }

  private onlyDigits(value: string): string {
    return (value || '').toString().replace(/\D/g, '');
  }

  onSubmitAccess() {
    if (this.accessForm.valid) {
      const personal = this.personalDataForm.value;
      const access = this.accessForm.value;

      const payload = {
        fullName: personal.fullName,
        cpf: this.onlyDigits(personal.cpf),
        email: access.email,
        accountType: 'DIGITAL',
        password: access.password,
        confirmPassword: access.confirmPassword,
      };

      this.createAccountService.createAccount(payload).subscribe({
        next: (res) => {
          this.toast.show('Conta criada com sucesso!', 'success', 6000);
          this.router.navigate(['login']);
        },
        error: (err) => {
          this.toast.show(err?.error?.message, 'error', 4000);          
          console.error(err);
        },
      });
    } else {
      this.accessForm.markAllAsTouched();
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

  getFullNameError(): string {
    const control = this.personalDataForm.get('fullName');
    if (control?.hasError('required')) return 'Nome completo é obrigatório';
    if (control?.hasError('minlength')) return 'Nome deve ter pelo menos 3 caracteres';
    return '';
  }

  getCpfError(): string {
    const control = this.personalDataForm.get('cpf');
    if (control?.hasError('required')) return 'CPF é obrigatório';
    if (control?.hasError('pattern')) return 'CPF deve ter o formato 000.000.000-00';
    return '';
  }

  getBirthDateError(): string {
    const control = this.personalDataForm.get('birthDate');
    if (control?.hasError('required')) {
      return 'Data de nascimento é obrigatória';
    }
    return '';
  }

  getPhoneError(): string {
    const control = this.personalDataForm.get('phone');
    if (control?.hasError('required')) {
      return 'Telefone é obrigatório';
    }
    if (control?.hasError('pattern')) {
      return 'Telefone deve ter o formato (00) 00000-0000';
    }
    return '';
  }

  getEmailError(): string {
    const control = this.accessForm.get('email');
    if (control?.hasError('required')) {
      return 'Email é obrigatório';
    }
    if (control?.hasError('email')) {
      return 'Email deve ter um formato válido';
    }
    return '';
  }

  getPasswordError(): string {
    const control = this.accessForm.get('password');
    if (control?.hasError('required')) {
      return 'Senha é obrigatória';
    }
    if (control?.hasError('minlength')) {
      return 'Senha deve ter pelo menos 8 caracteres';
    }
    return '';
  }

  getConfirmPasswordError(): string {
    const control = this.accessForm.get('confirmPassword');
    if (control?.hasError('required')) {
      return 'Confirmação de senha é obrigatória';
    }
    if (this.accessForm.hasError('passwordsMismatch') && control?.touched) {
      return 'As senhas não conferem';
    }
    return '';
  }
}
