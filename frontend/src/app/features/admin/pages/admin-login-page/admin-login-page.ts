import { afterNextRender, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../../core/services/auth.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-admin-login-page',
  imports: [ReactiveFormsModule],
  templateUrl: './admin-login-page.html',
  styleUrl: './admin-login-page.scss',
})
export class AdminLoginPage {
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);

  loading = signal(false);

  readonly form = this.formBuilder.nonNullable.group({
    email: ['admin@groveroot.com', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  constructor() {
    afterNextRender(() => {
      if (this.authService.isAdmin()) {
        this.router.navigate(['/admin/products']);
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);

    this.authService.login(this.form.getRawValue()).subscribe({
      next: (user) => {
        this.loading.set(false);

        if (user.role !== 'ADMIN') {
          this.notificationService.error('Only admin accounts can access this area.');
          return;
        }

        this.router.navigate(['/admin/products']);
      },
      error: (err) => {
        this.loading.set(false);
        this.notificationService.error(err?.error?.message ?? 'Login failed. Check your email and password.');
      },
    });
  }
}
