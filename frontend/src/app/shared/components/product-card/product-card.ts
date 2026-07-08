import { CurrencyPipe } from '@angular/common';
import { Component, input } from '@angular/core';

import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-card',
  imports: [CurrencyPipe],
  templateUrl: './product-card.html',
  styleUrl: './product-card.scss',
})
export class ProductCard {
  product = input.required<Product>();

  categoryIcon(categoryName?: string): string {
    const name = categoryName?.toLowerCase() ?? '';

    if (name.includes('iphone') || name.includes('phone')) return '📱';
    if (name.includes('mac') || name.includes('laptop')) return '💻';
    if (name.includes('ipad') || name.includes('tablet')) return '📟';
    if (name.includes('watch')) return '⌚';
    if (name.includes('airpod') || name.includes('audio')) return '🎧';

    return '🍎';
  }
}
