import { Component, signal, OnInit, HostListener, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Sidebar } from './components/sidebar/sidebar';
import { filter } from 'rxjs';
import { ToastContainerComponent } from './components/toast/toast-container/toast-container.component';
import { AuthService } from './shared/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatIconModule, MatButtonModule, MatToolbarModule, Sidebar, ToastContainerComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  protected readonly title = signal('capbank-frontend');
  private authService = inject(AuthService);

  isMobile = signal(false);
  sidebarOpen = signal(false);
  routeShowSidebar = signal(false);
  activeUrl = signal('');

  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.updateSidebarVisibility(event.urlAfterRedirects);
      });
  }

  private updateSidebarVisibility(url: string): void {
    const hideSidebarRoutes = ['/login', '/create-account'];
    this.activeUrl.set(url);
    this.routeShowSidebar.set(!hideSidebarRoutes.some((route) => url.startsWith(route)));
  }

  get isCreateAccountPage(): boolean {
    return this.activeUrl() === '/create-account';
  }

  ngOnInit(): void {
    this.checkScreenSize();
  }

  @HostListener('window:resize')
  onResize(): void {
    this.checkScreenSize();
  }

  private checkScreenSize(): void {
    this.isMobile.set(window.innerWidth < 768);
    if (!this.isMobile()) {
      this.sidebarOpen.set(true);
    } else {
      this.sidebarOpen.set(false);
    }
  }

  toggleSidebar(): void {
    this.sidebarOpen.update((open) => !open);
  }

  closeSidebar(): void {
    this.sidebarOpen.set(false);
  }

  onMenuClick(menuId: string): void {
    console.log('Menu clicked:', menuId);
  }
  
  logout() {
    this.authService.logout();
  }

}
