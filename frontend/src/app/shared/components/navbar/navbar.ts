import { Component, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { NavbarCart } from '../navbar-cart/navbar-cart';

export interface NavLink {
  label: string;
  path: string;
}

@Component({
  selector: 'app-navbar',
  imports: [NavbarCart, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  private readonly router = inject(Router);
  readonly authService = inject(AuthService);

  mobileOpen = signal(false);
  private readonly currentPath = signal(this.getPathname(this.router.url));

  readonly navLinks: NavLink[] = [
    { label: 'Home', path: '' },
    { label: 'Products', path: 'products' },
    { label: 'Orchard', path: 'orchard' },
    { label: 'Recipes', path: 'recipes' },
    { label: 'About', path: 'about' },
  ];

  constructor() {
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        this.currentPath.set(this.getPathname(event.urlAfterRedirects));
        this.mobileOpen.set(false);
      });
  }

  toggleMobile(): void {
    this.mobileOpen.update((open) => !open);
  }

  closeMobile(): void {
    this.mobileOpen.set(false);
  }

  navigate(path: string): void {
    this.router.navigate(path === '' ? ['/'] : ['/', path]);
    this.mobileOpen.set(false);
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/']),
    });
    this.mobileOpen.set(false);
  }

  isActive(path: string): boolean {
    const url = this.currentPath();

    if (path === '') {
      return url === '/' || url === '';
    }

    return url === `/${path}` || url.startsWith(`/${path}/`);
  }

  private getPathname(url: string): string {
    return url.split('?')[0].split('#')[0];
  }
}
