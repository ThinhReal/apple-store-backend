import { CurrencyPipe, DatePipe, isPlatformBrowser } from '@angular/common';
import { afterNextRender, Component, inject, PLATFORM_ID, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Order } from '../../../../core/models/order.model';
import { NotificationService } from '../../../../core/services/notification.service';
import { OrderService } from '../../../../core/services/order.service';

@Component({
  selector: 'app-my-orders-page',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './my-orders-page.html',
  styleUrl: './my-orders-page.scss',
})
export class MyOrdersPage {
  private readonly orderService = inject(OrderService);
  private readonly notificationService = inject(NotificationService);
  private readonly platformId = inject(PLATFORM_ID);

  orders = signal<Order[]>([]);
  loading = signal(true);

  constructor() {
    afterNextRender(() => this.loadOrders());
  }

  loadOrders(): void {
    if (!isPlatformBrowser(this.platformId)) {
      this.loading.set(false);
      return;
    }

    this.loading.set(true);

    this.orderService.getMyOrders().subscribe({
      next: (orders) => {
        this.orders.set(orders);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.notificationService.error(err?.error?.message ?? 'Could not load your orders.');
      },
    });
  }

  itemCount(order: Order): number {
    return (order.order_items ?? []).reduce((total, item) => total + item.quantity, 0);
  }
}
