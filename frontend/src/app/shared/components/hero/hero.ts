import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

export interface HeroStat {
  num: string;
  label: string;
}

export interface HeroQuickNav {
  label: string;
  path: string;
}

@Component({
  selector: 'app-hero',
  imports: [],
  templateUrl: './hero.html',
  styleUrl: './hero.scss',
})
export class Hero {
  private readonly router = inject(Router);

  readonly quickNav: HeroQuickNav[] = [
    { label: 'Orchard', path: 'orchard' },
    { label: 'Products', path: 'products' },
    { label: 'Recipes', path: 'recipes' },
    { label: 'About', path: 'about' },
  ];

  readonly stats: HeroStat[] = [
    { num: '40+', label: 'Apple Varieties' },
    { num: '1892', label: 'Est. Year' },
    { num: '100%', label: 'Organic Grown' },
  ];

  navigate(path: string): void {
    this.router.navigate(path === '' ? ['/'] : ['/', path]);
  }
}
