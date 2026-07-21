export interface OrderItemRequest {
  product_id: string;
  quantity: number;
}

export interface CheckoutRequest {
  order_items: OrderItemRequest[];
}

export interface OrderItem {
  id?: number;
  product_id: string;
  product_name?: string;
  quantity: number;
  unit_price: number;
}

export interface Order {
  id: number;
  user_id?: number;
  customer_email?: string;
  customer_name?: string;
  order_date?: string;
  total_amount: number;
  status?: string;
  order_items?: OrderItem[];
}

export const ORDER_STATUSES = [
  'PENDING',
  'PROCESSING',
  'SHIPPED',
  'DELIVERED',
  'CANCELLED',
] as const;

export type OrderStatus = (typeof ORDER_STATUSES)[number];

export interface UpdateOrderStatusRequest {
  status: OrderStatus;
}
