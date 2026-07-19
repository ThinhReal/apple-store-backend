import { CurrencyPipe } from '@angular/common';
import { Component, input } from '@angular/core';

import { Product } from '../../models/product.model';
import { getProductImageUrl } from '../../utils/product-image.util';

@Component({
  selector: 'app-product-card',
  imports: [CurrencyPipe],
  templateUrl: './product-card.html',
  styleUrl: './product-card.scss',
})
export class ProductCard {
  product = input.required<Product>();

  productImage(): string {
    return getProductImageUrl(this.product());
  }
}
