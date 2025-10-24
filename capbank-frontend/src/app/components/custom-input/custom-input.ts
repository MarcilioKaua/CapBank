import { Component, Input, forwardRef, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-custom-input',
  templateUrl: './custom-input.html',
  styleUrls: ['./custom-input.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CustomInputComponent),
      multi: true
    }
  ]
})
export class CustomInputComponent implements ControlValueAccessor, OnInit {
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() type: string = 'text';
  @Input() prefixText: string = '';
  @Input() prefixIcon: string = '';
  @Input() suffixIcon: string = '';
  @Input() errorMessage: string = '';
  @Input() required: boolean = false;
  @Input() disabled: boolean = false;
  @Input() rows: number = 1; 
  @Input() min: number | null = null;
  @Input() max: number | null = null;
  @Input() step: number | null = null;
  @Input() showPasswordToggle: boolean = false; 

  value: any = '';
  isFocused: boolean = false;
  isTouched: boolean = false;
  showPassword: boolean = false;

  
  onChange: any = () => {};
  onTouched: any = () => {};

  ngOnInit() {
  
  }

  onInputChange(event: any) {
    const value = event.target.value;
    this.value = value;
    this.onChange(value);
  }

  onBlur() {
    this.isFocused = false;
    this.isTouched = true;
    this.onTouched();
  }

  onFocus() {
    this.isFocused = true;
  }

  writeValue(value: any): void {
    this.value = value || '';
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  get shouldFloat(): boolean {
    return this.isFocused || (this.value !== null && this.value !== '');
  }

  get isTextarea(): boolean {
    return this.rows > 1;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  get inputType(): string {
    if (this.type === 'password' && this.showPasswordToggle) {
      return this.showPassword ? 'text' : 'password';
    }
    return this.type;
  }
}
