import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

export interface NavLink {
  label: string;
  path: string;
}

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  private readonly router = inject(Router);

  mobileOpen = signal(false);

  readonly navLinks: NavLink[] = [
    { label: 'Home', path: '' },
    { label: 'Products', path: 'products' },
    { label: 'Orchard', path: 'orchard' },
    { label: 'Recipes', path: 'recipes' },
    { label: 'About', path: 'about' },
  ];

  toggleMobile(): void {
    this.mobileOpen.update((open) => !open);
  }

  navigate(path: string): void {
    this.router.navigate(path === '' ? ['/'] : ['/', path]);
    this.mobileOpen.set(false);
  }

  isActive(path: string): boolean {
    const url = this.router.url;

    if (path === '') {
      return url === '/' || url === '';
    }

    return url === `/${path}` || url.startsWith(`/${path}/`);
  }
}
