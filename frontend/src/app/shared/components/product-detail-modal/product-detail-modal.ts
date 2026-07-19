import { DatePipe } from '@angular/common';
import { Component, HostListener, input, output } from '@angular/core';

import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-detail-modal',
  imports: [DatePipe],
  templateUrl: './product-detail-modal.html',
  styleUrl: './product-detail-modal.scss',
})
export class ProductDetailModal {
  product = input.required<Product>();
  detailImage = input.required<string>();
  badge = input<string | null>(null);
  flavorLabel = input('');
  weightLabel = input('each');
  loading = input(false);
  inCart = input(false);
  inWishlist = input(false);

  close = output<void>();
  toggleWishlist = output<void>();
  addToCart = output<void>();

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
