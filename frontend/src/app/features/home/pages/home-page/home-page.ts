import { afterNextRender, Component, inject, signal } from '@angular/core';

import { ProductService } from '../../../products/services/product.service';
import { ProductCard } from '../../../../shared/components/product-card/product-card';
import { Product } from '../../../../shared/models/product.model';

@Component({
  selector: 'app-home-page',
  imports: [ProductCard],
  templateUrl: './home-page.html',
  styleUrl: './home-page.scss',
})
export class HomePage {
  private readonly productService = inject(ProductService);

  products = signal<Product[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    afterNextRender(() => this.loadProducts());
  }

  loadProducts(): void {
    this.loading.set(true);
    this.error.set(null);

    this.productService.getAllProducts().subscribe({
      next: (products) => {
        this.products.set(products);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load products. Make sure the backend is running on port 8080.');
        this.loading.set(false);
      },
    });
  }
}
