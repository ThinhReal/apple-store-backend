import { isPlatformBrowser } from '@angular/common';
import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, of } from 'rxjs';

import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const platformId = inject(PLATFORM_ID);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  const authService = inject(AuthService);
  const router = inject(Router);

  const check = authService.authChecked()
    ? of(authService.isAdmin())
    : authService.loadCurrentUser().pipe(map(() => authService.isAdmin()));

  return check.pipe(
    map((isAdmin) => isAdmin || router.createUrlTree(['/admin/login']))
  );
};
