import { CurrencyPipe } from '@angular/common';
import { afterNextRender, Component, computed, inject, signal } from '@angular/core';

import { ProductService } from '../../services/product.service';
import { Product } from '../../../../shared/models/product.model';

@Component({
  selector: 'app-product-page',
  imports: [CurrencyPipe],
  templateUrl: './product-page.html',
  styleUrl: './product-page.scss',
})
export class ProductPage {
  private readonly productService = inject(ProductService);

  private readonly defaultImage =
    'https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=500&h=500&fit=crop&auto=format';

  products = signal<Product[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  activeCategory = signal('All');
  cart = signal<Set<number>>(new Set());
  wishlist = signal<Set<number>>(new Set());

  categories = computed(() => {
    const names = this.products()
      .map((product) => product.category?.name)
      .filter((name): name is string => Boolean(name));

    return ['All', ...Array.from(new Set(names)).sort()];
  });

  filteredProducts = computed(() => {
    const category = this.activeCategory();
    const products = this.products();

    if (category === 'All') {
      return products;
    }

    return products.filter((product) => product.category?.name === category);
  });

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

  setCategory(category: string): void {
    this.activeCategory.set(category);
  }

  toggleCart(id: number): void {
    this.cart.update((current) => {
      const next = new Set(current);

      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }

      return next;
    });
  }

  toggleWishlist(id: number, event: Event): void {
    event.stopPropagation();

    this.wishlist.update((current) => {
      const next = new Set(current);

      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }

      return next;
    });
  }

  isInCart(id: number): boolean {
    return this.cart().has(id);
  }

  isInWishlist(id: number): boolean {
    return this.wishlist().has(id);
  }

  productImage(product: Product): string {
    const categoryName = product.category?.name?.toLowerCase() ?? '';

    if (categoryName.includes('cider') || categoryName.includes('juice')) {
      return 'https://images.unsplash.com/photo-1535914254981-b5012eebbd15?w=500&h=500&fit=crop&auto=format';
    }

    if (categoryName.includes('gift')) {
      return 'https://images.unsplash.com/photo-1510627489930-0c1b0bfb6785?w=500&h=500&fit=crop&auto=format';
    }

    return this.defaultImage;
  }

  productBadge(product: Product): string | null {
    if (product.stock_quantity === 0) {
      return 'Out of Stock';
    }

    if (product.stock_quantity <= 5) {
      return 'Low Stock';
    }

    return null;
  }

  stockLabel(product: Product): string {
    if (product.stock_quantity === 0) {
      return 'Out of stock';
    }

    return `${product.stock_quantity} in stock`;
  }
}
