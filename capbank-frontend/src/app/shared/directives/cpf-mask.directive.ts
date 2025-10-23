import { Directive, HostListener } from '@angular/core';
import { NgControl } from '@angular/forms';

/**
 * Diretiva standalone que formata CPF no input seguindo o padrão XXX.XXX.XXX-XX.
 * Ela mantém o value do form control no formato mascarado (compatível com as patterns do form).
 */
@Directive({
  selector: '[cpfMask]',
  standalone: true
})
export class CpfMaskDirective {
  constructor(private control: NgControl) {}

  // quando o usuário digita
  @HostListener('input', ['$event'])
  onInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, ''); // só dígitos

    // limita a 11 dígitos (CPF)
    if (value.length > 11) value = value.slice(0, 11);

    // aplica máscara conforme comprimento
    if (value.length > 9) {
      value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4');
    } else if (value.length > 6) {
      value = value.replace(/(\d{3})(\d{3})(\d{0,3})/, '$1.$2.$3');
    } else if (value.length > 3) {
      value = value.replace(/(\d{3})(\d{0,3})/, '$1.$2');
    }

    this.control.control?.setValue(value, { emitEvent: false });
  }

  @HostListener('paste', ['$event'])
  onPaste(e: ClipboardEvent) {
    const pasted = e.clipboardData?.getData('text') ?? '';
    const digits = pasted.replace(/\D/g, '').slice(0, 11);
    // impede que o evento padrão cole algo diferente do formato: previne e aplica a máscara manualmente
    e.preventDefault();
    let formatted = digits;
    if (digits.length > 9) formatted = digits.replace(/(\d{3})(\d{3})(\d{3})(\d{0,2})/, '$1.$2.$3-$4');
    else if (digits.length > 6) formatted = digits.replace(/(\d{3})(\d{3})(\d{0,3})/, '$1.$2.$3');
    else if (digits.length > 3) formatted = digits.replace(/(\d{3})(\d{0,3})/, '$1.$2');

    this.control.control?.setValue(formatted, { emitEvent: false });
  }
}
