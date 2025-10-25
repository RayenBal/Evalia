import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth-service/auth.service';

export const panelisteGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  // Pas connecté → on renvoie vers le login avec l'URL de retour
  if (!auth.isAuthenticated()) {
    router.navigate(['/login'], { queryParams: { r: state.url } });
    return false;
  }

  // Mauvais type d’utilisateur → on renvoie à l’accueil (ou une 403)
  if (auth.userType !== 'Paneliste') {
    router.navigate(['/'], { replaceUrl: true });
    return false;
  }

  return true;
};