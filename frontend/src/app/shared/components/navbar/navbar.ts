import { Component, inject, signal } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

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

  navigate(path: string): void {
    this.router.navigate(path === '' ? ['/'] : ['/', path]);
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
