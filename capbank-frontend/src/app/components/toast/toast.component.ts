import { Component, Input } from '@angular/core';
import { Toast } from 'src/app/shared/models/toast.model';

@Component({
  selector: 'app-toast',
  imports: [],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.css'
})
export class ToastComponent {
  @Input() toast!: Toast;
  
  iconFor(type: string) {
    switch (type) {
      case 'success': return '✔';
      case 'error': return '✖';
      case 'warning': return '⚠';
      default: return 'ℹ';
    }
}
}
