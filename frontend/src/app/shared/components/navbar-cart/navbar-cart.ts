import { CurrencyPipe } from '@angular/common';
import { Component, ElementRef, HostListener, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-navbar-cart',
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './navbar-cart.html',
  styleUrl: './navbar-cart.scss',
})
export class NavbarCart {
  private readonly cartService = inject(CartService);
  private readonly elementRef = inject(ElementRef);

  readonly cartItems = this.cartService.cartItems;
  readonly totalItems = this.cartService.totalItems;
  readonly totalPrice = this.cartService.totalPrice;

  dropdownOpen = signal(false);

  toggleDropdown(event: Event): void {
    event.stopPropagation();
    this.dropdownOpen.update((open) => !open);
  }

  closeDropdown(): void {
    this.dropdownOpen.set(false);
  }

  incrementQuantity(productId: string, currentQuantity: number, stockQuantity: number): void {
    if (currentQuantity >= stockQuantity) {
      return;
    }

    this.cartService.updateQuantity(productId, currentQuantity + 1);
  }

  decrementQuantity(productId: string, currentQuantity: number): void {
    this.cartService.updateQuantity(productId, currentQuantity - 1);
  }

  removeItem(productId: string, event: Event): void {
    event.stopPropagation();
    this.cartService.removeItem(productId);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.dropdownOpen()) {
      return;
    }

    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.closeDropdown();
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.closeDropdown();
  }
}
