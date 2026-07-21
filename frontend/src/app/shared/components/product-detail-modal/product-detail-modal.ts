import { DatePipe } from '@angular/common';
import { Component, HostListener, inject, input, output, signal } from '@angular/core';

import { CartService } from '../../../core/services/cart.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-detail-modal',
  imports: [DatePipe],
  templateUrl: './product-detail-modal.html',
  styleUrl: './product-detail-modal.scss',
})
export class ProductDetailModal {
  private readonly cartService = inject(CartService);

  product = input.required<Product>();
  detailImage = input.required<string>();
  badge = input<string | null>(null);
  flavorLabel = input('');
  weightLabel = input('each');
  loading = input(false);
  inCart = input(false);
  cartQuantity = input(0);
  inWishlist = input(false);

  quantity = signal(1);

  close = output<void>();
  toggleWishlist = output<void>();

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.close.emit();
  }

  onBackdropClick(): void {
    this.close.emit();
  }

  onPanelClick(event: Event): void {
    event.stopPropagation();
  }

  onWishlistClick(event: Event): void {
    event.stopPropagation();
    this.toggleWishlist.emit();
  }

  decrementQuantity(): void {
    this.quantity.update((value) => Math.max(1, value - 1));
  }

  incrementQuantity(): void {
    this.quantity.update((value) => Math.min(this.product().stock_quantity, value + 1));
  }

  addToCart(): void {
    this.cartService.addProduct(this.product(), this.quantity());
  }

  displayValue(value?: string | number | null): string {
    if (value === null || value === undefined || value === '') {
      return '—';
    }
    return String(value);
  }

  hasFlavorProfile(): boolean {
    const profile = this.product().flavor_profile;
    if (!profile) {
      return false;
    }

    return Boolean(
      profile.overall_profile ||
        profile.tasting_description ||
        profile.sweetness_level != null ||
        profile.tartness_level != null ||
        (profile.dominant_notes?.length ?? 0) > 0
    );
  }
}
