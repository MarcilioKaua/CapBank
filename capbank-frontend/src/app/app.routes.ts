import { Routes } from '@angular/router';
import { Login } from './components/user/login/login';
import { Register } from './components/user/register/register';

export const routes: Routes = [
  { path: '', component: Login },
  { path: 'login', component: Login },
  { path: 'register', component: Register }
];
