import { CurrencyPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { CartService } from '../../../../core/services/cart.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { OrderService } from '../../../../core/services/order.service';
import { Order } from '../../../../core/models/order.model';

@Component({
  selector: 'app-checkout-page',
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './checkout-page.html',
  styleUrl: './checkout-page.scss',
})
export class CheckoutPage {
  private readonly cartService = inject(CartService);
  private readonly orderService = inject(OrderService);
  private readonly notificationService = inject(NotificationService);

  placingOrder = signal(false);
  completedOrder = signal<Order | null>(null);

  readonly cartItems = this.cartService.cartItems;
  readonly totalItems = this.cartService.totalItems;
  readonly totalPrice = this.cartService.totalPrice;

  placeOrder(): void {
    const items = this.cartItems();

    if (items.length === 0) {
      this.notificationService.error('Your cart is empty.');
      return;
    }

    this.placingOrder.set(true);

    this.orderService.checkout(items).subscribe({
      next: (order) => {
        this.placingOrder.set(false);
        this.cartService.clearCart();
        this.completedOrder.set(order);
        this.notificationService.success(`Order #${order.id} placed successfully.`);
      },
      error: (err) => {
        this.placingOrder.set(false);
        this.notificationService.error(this.resolveCheckoutError(err));
      },
    });
  }

  private resolveCheckoutError(err: { status?: number; error?: { message?: string } }): string {
    if (err?.error?.message) {
      return err.error.message;
    }

    if (err.status === 409) {
      return 'The product stock has been updated by another user. Please review your cart.';
    }

    return 'Could not place your order.';
  }
}
