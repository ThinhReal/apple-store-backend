import { isPlatformBrowser } from '@angular/common';
import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, of } from 'rxjs';

import { AuthService } from '../services/auth.service';

export const customerGuard: CanActivateFn = (_route, state) => {
  const platformId = inject(PLATFORM_ID);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  const authService = inject(AuthService);
  const router = inject(Router);

  const resolve = () => {
    if (!authService.isCustomer()) {
      return router.createUrlTree(['/login'], {
        queryParams: { returnUrl: state.url },
      });
    }

    return true;
  };

  if (authService.authChecked()) {
    return of(resolve());
  }

  return authService.loadCurrentUser().pipe(map(() => resolve()));
};
