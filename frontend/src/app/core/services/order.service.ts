import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config/api.config';
import { CartItem } from '../models/cart.model';
import { CheckoutRequest, Order, OrderStatus, UpdateOrderStatusRequest } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/orders`;

  checkout(cartItems: CartItem[]): Observable<Order> {
    const payload: CheckoutRequest = {
      order_items: cartItems.map((item) => ({
        product_id: item.productId,
        quantity: item.quantity,
      })),
    };

    return this.http.post<Order>(`${this.baseUrl}/checkout`, payload);
  }

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.baseUrl}/me`);
  }

  getAllOrdersForAdmin(): Observable<Order[]> {
    return this.http.get<Order[]>(`${API_BASE_URL}/admin/orders`);
  }

  updateOrderStatus(orderId: number, status: OrderStatus): Observable<Order> {
    const payload: UpdateOrderStatusRequest = { status };
    return this.http.patch<Order>(`${API_BASE_URL}/admin/orders/${orderId}/status`, payload);
  }
}
