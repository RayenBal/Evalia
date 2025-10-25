import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth-service/auth.service';

export const annonceurGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    router.navigate(['/login'], { queryParams: { r: state.url } });
    return false;
  }
  if (auth.userType !== 'Announceur') {
    router.navigate(['/']); // ou une page 403
    return false;
  }
  return true;
};