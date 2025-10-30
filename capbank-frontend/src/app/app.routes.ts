import { Routes } from '@angular/router';
import { authGuard } from './shared/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then(c => c.Login)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard').then(c => c.Dashboard),
    canActivate:[authGuard]
  },
  {
    path: 'transfers',
    loadComponent: () => import('./pages/transfers/transfers').then(c => c.Transfers),
    canActivate:[authGuard]
  },
  {
    path: 'extract',
    loadComponent: () => import('./pages/extract/extract').then(c => c.Extract),
    canActivate:[authGuard]
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/profile/profile').then(c => c.Profile),
    canActivate:[authGuard]
  },
  {
    path: 'settings',
    loadComponent: () => import('./pages/settings/settings').then(c => c.Settings),
    canActivate:[authGuard]
  },
  {
    path: 'create-account',
    loadComponent: () => import('./pages/create-account/create-account').then(c => c.CreateAccount)
  },
  { path: '**', redirectTo: '/login' }
];
