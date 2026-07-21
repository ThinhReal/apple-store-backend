import { isPlatformBrowser } from '@angular/common';
import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';

import { Toast, ToastType } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly toasts = signal<Toast[]>([]);
  private nextId = 0;

  readonly messages = this.toasts.asReadonly();

  success(message: string, duration = 4000): string {
    return this.show('success', message, duration);
  }

  error(message: string, duration = 6000): string {
    return this.show('error', message, duration);
  }

  info(message: string, duration = 4000): string {
    return this.show('info', message, duration);
  }

  warning(message: string, duration = 5000): string {
    return this.show('warning', message, duration);
  }

  dismiss(id: string): void {
    this.toasts.update((items) => items.filter((item) => item.id !== id));
  }

  private show(type: ToastType, message: string, duration: number): string {
    const id = `toast-${++this.nextId}`;
    this.toasts.update((items) => [...items, { id, type, message }]);

    if (isPlatformBrowser(this.platformId) && duration > 0) {
      window.setTimeout(() => this.dismiss(id), duration);
    }

    return id;
  }
}
