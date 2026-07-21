import { isPlatformBrowser } from '@angular/common';
import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, of } from 'rxjs';

import { AuthService } from '../services/auth.service';
import { CartService } from '../services/cart.service';

export const checkoutGuard: CanActivateFn = () => {
  const platformId = inject(PLATFORM_ID);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  const authService = inject(AuthService);
  const cartService = inject(CartService);
  const router = inject(Router);

  if (cartService.totalItems() === 0) {
    return router.createUrlTree(['/products']);
  }

  const resolve = () => {
    if (!authService.isCustomer()) {
      return router.createUrlTree(['/login'], { queryParams: { returnUrl: '/checkout' } });
    }

    return true;
  };

  if (authService.authChecked()) {
    return of(resolve());
  }

  return authService.loadCurrentUser().pipe(map(() => resolve()));
};
