import { isPlatformBrowser } from '@angular/common';
import { computed, inject, Injectable, PLATFORM_ID, signal } from '@angular/core';

import { CartItem } from '../models/cart.model';
import { Product } from '../../shared/models/product.model';
import { getProductImageUrl } from '../../shared/utils/product-image.util';

const STORAGE_KEY = 'applestore.cart';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly items = signal<CartItem[]>(this.loadFromStorage());

  readonly cartItems = this.items.asReadonly();

  readonly totalItems = computed(() =>
    this.items().reduce((total, item) => total + item.quantity, 0),
  );

  readonly totalPrice = computed(() =>
    this.items().reduce((total, item) => total + item.price * item.quantity, 0),
  );

  addProduct(product: Product, quantity = 1): void {
    if (quantity <= 0 || product.stock_quantity === 0) {
      return;
    }

    const productId = String(product.id);
    const nextItems = [...this.items()];
    const existingIndex = nextItems.findIndex((item) => item.productId === productId);
    const maxQuantity = product.stock_quantity;

    if (existingIndex >= 0) {
      const existing = nextItems[existingIndex];
      const nextQuantity = Math.min(existing.quantity + quantity, maxQuantity);
      nextItems[existingIndex] = {
        ...existing,
        name: product.name,
        price: product.price,
        imageUrl: getProductImageUrl(product),
        stockQuantity: maxQuantity,
        quantity: nextQuantity,
      };
    } else {
      nextItems.push({
        productId,
        name: product.name,
        price: product.price,
        imageUrl: getProductImageUrl(product),
        stockQuantity: maxQuantity,
        quantity: Math.min(quantity, maxQuantity),
      });
    }

    this.persist(nextItems);
  }

  updateQuantity(productId: string, quantity: number): void {
    if (quantity <= 0) {
      this.removeItem(productId);
      return;
    }

    const nextItems = this.items().map((item) => {
      if (item.productId !== productId) {
        return item;
      }

      return {
        ...item,
        quantity: Math.min(quantity, item.stockQuantity),
      };
    });

    this.persist(nextItems);
  }

  removeItem(productId: string): void {
    this.persist(this.items().filter((item) => item.productId !== productId));
  }

  clearCart(): void {
    this.persist([]);
  }

  isInCart(productId: Product['id']): boolean {
    return this.items().some((item) => item.productId === String(productId));
  }

  getQuantity(productId: Product['id']): number {
    return this.items().find((item) => item.productId === String(productId))?.quantity ?? 0;
  }

  private persist(items: CartItem[]): void {
    this.items.set(items);

    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
  }

  private loadFromStorage(): CartItem[] {
    if (!isPlatformBrowser(this.platformId)) {
      return [];
    }

    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) {
        return [];
      }

      const parsed = JSON.parse(raw) as CartItem[];
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }
}
