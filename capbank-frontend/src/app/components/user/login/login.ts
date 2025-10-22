import { Component, OnInit } from '@angular/core';
import { FloatLabelModule } from 'primeng/floatlabel';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { FluidModule } from 'primeng/fluid';

import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { TextareaModule } from 'primeng/textarea';

@Component({
  selector: 'app-login',
  imports: [FloatLabelModule, FormsModule, RouterModule, CommonModule, ReactiveFormsModule, 
    FluidModule, InputTextModule, 
    ButtonModule, SelectModule, TextareaModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  formulario!: FormGroup;
  loading = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
      this.formulario = this.fb.group({
        login: ['', [Validators.required, Validators.minLength(11)]],
        password: ['', [Validators.required, Validators.minLength(6)]],
      });
  }

  onSubmit(): void {
    if (this.formulario.valid) {
      this.loading = true;
      const { login, password } = this.formulario.value;
    }
  }
}
