import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

export interface FooterLink {
  label: string;
  path: string;
  queryParams?: Record<string, string>;
}

export interface FooterSection {
  heading: string;
  links: FooterLink[];
}

@Component({
  selector: 'app-footer',
  imports: [],
  templateUrl: './footer.html',
  styleUrl: './footer.scss',
})
export class Footer {
  private readonly router = inject(Router);

  readonly socialIcons = ['📷', '📘', '📌'];

  readonly legalLinks: FooterLink[] = [
    { label: 'Privacy Policy', path: 'about' },
    { label: 'Terms of Use', path: 'about' },
    { label: 'Accessibility', path: 'about' },
  ];

  readonly footerSections: FooterSection[] = [
    {
      heading: 'Shop',
      links: [
        { label: 'All Products', path: 'products' },
        { label: 'Seasonal Picks', path: 'products', queryParams: { category: 'Cider & Juice' } },
        { label: 'Gift Boxes', path: 'products', queryParams: { category: 'Gift Boxes' } },
      ],
    },
    {
      heading: 'Our Story',
      links: [
        { label: 'About Us', path: 'about' },
        { label: 'The Orchard', path: 'orchard' },
        { label: 'Our Values', path: 'about' },
      ],
    },
    {
      heading: 'Support',
      links: [
        { label: 'Contact', path: 'about' },
        { label: 'FAQ', path: 'about' },
        { label: 'Shipping', path: 'about' },
      ],
    },
  ];

  navigate(path: string, queryParams?: Record<string, string>): void {
    const normalized = path === '' || path === 'home' ? '' : path;

    this.router.navigate(normalized === '' ? ['/'] : ['/', normalized], {
      queryParams: queryParams ?? null,
    });
  }
}
