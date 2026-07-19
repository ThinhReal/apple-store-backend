import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

export interface FooterLink {
  label: string;
  path: string;
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

  readonly legalLinks = ['Privacy Policy', 'Terms of Use', 'Accessibility'];

  readonly footerSections: FooterSection[] = [
    {
      heading: 'Shop',
      links: [
        { label: 'All Products', path: 'products' },
        { label: 'Seasonal Picks', path: 'products' },
        { label: 'Gift Boxes', path: 'products' },
      ],
    },
    {
      heading: 'Our Story',
      links: [
        { label: 'About Us', path: 'about' },
        { label: 'The Orchard', path: 'about' },
        { label: 'Our Values', path: 'about' },
      ],
    },
    {
      heading: 'Support',
      links: [
        { label: 'Contact', path: 'contact' },
        { label: 'FAQ', path: 'contact' },
        { label: 'Shipping', path: 'contact' },
      ],
    },
  ];

  navigate(path: string): void {
    const normalized = path === '' || path === 'home' ? '' : path;
    this.router.navigate(normalized === '' ? ['/'] : ['/', normalized]);
  }
}
