import { Component, inject } from '@angular/core';

import { DialogService } from '../../../core/services/dialog.service';

@Component({
  selector: 'app-confirm-dialog',
  imports: [],
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.scss',
})
export class ConfirmDialog {
  private readonly dialogService = inject(DialogService);

  readonly dialog = this.dialogService.confirmDialog;

  cancel(): void {
    this.dialogService.resolveConfirmDialog(false);
  }

  confirm(): void {
    this.dialogService.resolveConfirmDialog(true);
  }

  onBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.cancel();
    }
  }
}
