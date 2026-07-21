import { Component, inject } from '@angular/core';

import { NotificationService } from '../../../core/services/notification.service';
import { ToastType } from '../../../core/models/notification.model';

@Component({
  selector: 'app-toast-container',
  imports: [],
  templateUrl: './toast-container.html',
  styleUrl: './toast-container.scss',
})
export class ToastContainer {
  private readonly notificationService = inject(NotificationService);

  readonly messages = this.notificationService.messages;

  dismiss(id: string): void {
    this.notificationService.dismiss(id);
  }

  labelFor(type: ToastType): string {
    switch (type) {
      case 'success':
        return 'Success';
      case 'error':
        return 'Error';
      case 'warning':
        return 'Warning';
      default:
        return 'Notice';
    }
  }
}
