import { CurrencyPipe, DatePipe, isPlatformBrowser } from '@angular/common';
import { afterNextRender, Component, inject, PLATFORM_ID, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { ORDER_STATUSES, Order, OrderStatus } from '../../../../core/models/order.model';
import { AuthService } from '../../../../core/services/auth.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { OrderService } from '../../../../core/services/order.service';

@Component({
  selector: 'app-admin-order-page',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './admin-order-page.html',
  styleUrl: './admin-order-page.scss',
})
export class AdminOrderPage {
  private readonly orderService = inject(OrderService);
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly platformId = inject(PLATFORM_ID);

  orders = signal<Order[]>([]);
  loading = signal(true);
  updatingOrderId = signal<number | null>(null);

  readonly statuses = ORDER_STATUSES;

  constructor() {
    afterNextRender(() => this.loadOrders());
  }

  loadOrders(): void {
    if (!isPlatformBrowser(this.platformId)) {
      this.loading.set(false);
      return;
    }

    this.loading.set(true);

    this.orderService.getAllOrdersForAdmin().subscribe({
      next: (orders) => {
        this.orders.set(orders);
        this.loading.set(false);
      },
      error: (err) => {
        this.notificationService.error(err?.error?.message ?? 'Could not load orders.');
        this.loading.set(false);
      },
    });
  }

  updateStatus(order: Order, status: string): void {
    const nextStatus = status as OrderStatus;

    if (!nextStatus || order.status === nextStatus) {
      return;
    }

    this.updatingOrderId.set(order.id);

    this.orderService.updateOrderStatus(order.id, nextStatus).subscribe({
      next: (updatedOrder) => {
        this.updatingOrderId.set(null);
        this.orders.update((orders) =>
          orders.map((current) => (current.id === updatedOrder.id ? updatedOrder : current)),
        );
        this.notificationService.success(`Order #${order.id} updated to ${nextStatus}.`);
      },
      error: (err) => {
        this.updatingOrderId.set(null);
        this.notificationService.error(err?.error?.message ?? 'Could not update order status.');
        this.loadOrders();
      },
    });
  }

  itemCount(order: Order): number {
    return (order.order_items ?? []).reduce((total, item) => total + item.quantity, 0);
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/admin/login']),
    });
  }
}
