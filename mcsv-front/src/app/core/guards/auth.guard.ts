import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { SesionService } from '../login/services/sesion-service';

export const authGuard: CanActivateFn = () => {
  const sesionService = inject(SesionService);
  const router = inject(Router);

  if (sesionService.isAuthenticated()) {
    return true;
  }

  router.navigateByUrl('/login');
  return false;
};
