import { afterNextRender, Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { Footer } from '../../shared/components/footer/footer';
import { Navbar } from '../../shared/components/navbar/navbar';
import { ConfirmDialog } from '../../shared/components/confirm-dialog/confirm-dialog';
import { ToastContainer } from '../../shared/components/toast-container/toast-container';

@Component({
  selector: 'app-main-layout',
  imports: [ConfirmDialog, Footer, Navbar, RouterOutlet, ToastContainer],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.scss',
})
export class MainLayout {
  private readonly authService = inject(AuthService);

  constructor() {
    afterNextRender(() => {
      this.authService.loadCurrentUser().subscribe();
    });
  }
}
