import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard').then(c => c.Dashboard)
  },
  {
    path: 'transfers',
    loadComponent: () => import('./pages/transfers/transfers').then(c => c.Transfers)
  },
  {
    path: 'extract',
    loadComponent: () => import('./pages/extract/extract').then(c => c.Extract)
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/profile/profile').then(c => c.Profile)
  },
  {
    path: 'settings',
    loadComponent: () => import('./pages/settings/settings').then(c => c.Settings)
  },
  { path: '**', redirectTo: '/dashboard' }
];
