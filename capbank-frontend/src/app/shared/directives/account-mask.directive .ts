import { Directive, HostListener } from '@angular/core';
import { NgControl } from '@angular/forms';

/**
 * Diretiva standalone que formata Conta no input seguindo o padrão XXXXX-X.
 * Ela mantém o value do form control no formato mascarado (compatível com as patterns do form).
 */
@Directive({
  selector: '[AccountMask]',
  standalone: true
})
export class AccountMaskDirective {
  constructor(private control: NgControl) {}

  // quando o usuário digita
  @HostListener('input', ['$event'])
  onInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '').replace('e', ''); // só dígitos

    // limita a 6 dígitos (N. Conta)
    if (value.length > 6) value = value.slice(0, 8);

    // aplica máscara conforme comprimento
    if (value.length > 5) {
      value = value.replace(/(\d{5})(\d{3})/, '$1-$2');
    }
    
    this.control.control?.setValue(value, { emitEvent: false });
  }

  @HostListener('paste', ['$event'])
  onPaste(e: ClipboardEvent) {
    const pasted = e.clipboardData?.getData('text') ?? '';
    const digits = pasted.replace(/\D/g, '').slice(0, 8);
    // impede que o evento padrão cole algo diferente do formato: previne e aplica a máscara manualmente
    e.preventDefault();
    let formatted = digits;
    if (digits.length > 5) formatted = digits.replace(/(\d{5})(\d{3})/, '$1-$2');

    this.control.control?.setValue(formatted, { emitEvent: false });
  }
}
