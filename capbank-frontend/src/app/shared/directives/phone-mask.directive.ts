import { Directive, HostListener } from '@angular/core';
import { NgControl } from '@angular/forms';

/**
 * Diretiva standalone que aplica máscara de telefone:
 * - (XX) XXXXX-XXXX quando 11 dígitos (celular com 9)
 * - (XX) XXXX-XXXX quando 10 dígitos (fixo)
 */
@Directive({
  selector: '[phoneMask]',
  standalone: true
})
export class PhoneMaskDirective {
  constructor(private control: NgControl) {}

  @HostListener('input', ['$event'])
  onInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, ''); // só dígitos

    // limita a 11 (DDD + 9 dígitos) ou 10
    if (value.length > 11) value = value.slice(0, 11);

    if (value.length > 10) {
      // (XX) XXXXX-XXXX
      value = value.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
    } else if (value.length > 6) {
      // (XX) XXXX-XXXX
      value = value.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
    } else if (value.length > 2) {
      // (XX) XXXXX
      value = value.replace(/(\d{2})(\d{0,5})/, '($1) $2');
    }

    this.control.control?.setValue(value, { emitEvent: false });
  }

  @HostListener('paste', ['$event'])
  onPaste(e: ClipboardEvent) {
    const pasted = e.clipboardData?.getData('text') ?? '';
    const digits = pasted.replace(/\D/g, '').slice(0, 11);
    e.preventDefault();

    let formatted = digits;
    if (digits.length > 10) formatted = digits.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
    else if (digits.length > 6) formatted = digits.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
    else if (digits.length > 2) formatted = digits.replace(/(\d{2})(\d{0,5})/, '($1) $2');

    this.control.control?.setValue(formatted, { emitEvent: false });
  }
}
