import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MenuItem } from '../../shared/models/transaction.model';

@Component({
  selector: 'app-sidebar',
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatListModule,
    MatButtonModule
  ],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class Sidebar {
  @Input() isMobile = false;
  @Input() isOpen = false;
  @Output() menuClick = new EventEmitter<string>();
  @Output() close = new EventEmitter<void>();

  menuItems: MenuItem[] = [
    { id: 'dashboard', label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
    { id: 'transfers', label: 'Transferências', icon: 'swap_horiz', route: '/transfers' },
    { id: 'extract', label: 'Extrato', icon: 'receipt_long', route: '/extract' },
    { id: 'profile', label: 'Meu Perfil', icon: 'person', route: '/profile' },
    { id: 'settings', label: 'Configurações', icon: 'settings', route: '/settings' }
  ];

  constructor(private router: Router) {}

  onMenuClick(itemId: string): void {
    this.menuClick.emit(itemId);
    if (this.isMobile) {
      this.close.emit();
    }
  }

  onClose(): void {
    this.close.emit();
  }
}
