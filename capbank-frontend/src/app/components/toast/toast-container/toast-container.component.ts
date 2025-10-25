import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../shared/services/toast.service';

@Component({
    selector: 'app-toast-container',
    imports: [CommonModule],
    templateUrl: './toast-container.component.html',
    styleUrls: ['./toast-container.component.css'],
})
export class ToastContainerComponent {
    constructor(public toastService: ToastService) {}

    // um computed sinal que poderia formatar/ordenar os toasts se precisar
    readonly toasts = computed(() => this.toastService.toasts());

    iconFor(type: string) {
        switch (type) {
            case 'success': return '✔';
            case 'error': return '✖';
            case 'warning': return '⚠';
            default: return 'ℹ';
        }
    }
}