import { Injectable, signal } from '@angular/core';

import { ConfirmDialogOptions } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class DialogService {
  private readonly confirmState = signal<ConfirmDialogOptions | null>(null);
  private resolveConfirm: ((value: boolean) => void) | null = null;

  readonly confirmDialog = this.confirmState.asReadonly();

  confirm(options: ConfirmDialogOptions): Promise<boolean> {
    return new Promise((resolve) => {
      this.resolveConfirm = resolve;
      this.confirmState.set({
        confirmLabel: 'Confirm',
        cancelLabel: 'Cancel',
        variant: 'default',
        ...options,
      });
    });
  }

  resolveConfirmDialog(confirmed: boolean): void {
    this.confirmState.set(null);
    this.resolveConfirm?.(confirmed);
    this.resolveConfirm = null;
  }
}
