import { Injectable, signal } from '@angular/core';
import { Toast } from '../models/toast.model';

@Injectable({ providedIn: 'root' })
export class ToastService {
  // signal contendo o array de toasts
  readonly toasts = signal<Toast[]>([]);

  private generateId() {
    return Math.random().toString(36).slice(2, 9);
  }

  show(message: string, type: Toast['type'] = 'info', duration = 3000) {
    const toast: Toast = {
      id: this.generateId(),
      message,
      type,
      duration,
    };

    // adicionar ao final
    this.toasts.update((current) => [...current, toast]);

    // agendar remoção
    setTimeout(() => this.dismiss(toast.id), duration);

    return toast.id;
  }


  dismiss(id: string) {
    this.toasts.update((current) => current.filter((t) => t.id !== id));
  }

  clear() {
    this.toasts.set([]);
  }
}