import { Routes } from '@angular/router';
import { Login } from './core/login/login';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: Login,
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full',
  },
  {
    path: 'home',
    canActivate: [authGuard],
    loadComponent: async () => {
      const { Home } = await import('./features/home/home');
      return Home;
    },
  },
  {
    path: '**',
    redirectTo: '/login',
  },
];
