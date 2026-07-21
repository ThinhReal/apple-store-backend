import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../../core/services/auth.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login-page.html',
  styleUrl: './login-page.scss',
})
export class LoginPage {
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  readonly route = inject(ActivatedRoute);
  private readonly formBuilder = inject(FormBuilder);

  loading = signal(false);

  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.notificationService.error('Please enter a valid email and password.');
      return;
    }

    this.loading.set(true);

    this.authService.login(this.form.getRawValue()).subscribe({
      next: (user) => {
        this.loading.set(false);

        if (user.role === 'ADMIN') {
          this.notificationService.info('Use the admin login page to manage products.');
          this.router.navigate(['/admin/login']);
          return;
        }

        this.notificationService.success('Welcome back!');
        this.router.navigateByUrl(this.getReturnUrl());
      },
      error: (err) => {
        this.loading.set(false);
        this.notificationService.error(err?.error?.message ?? 'Login failed. Check your email and password.');
      },
    });
  }

  private getReturnUrl(): string {
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');

    if (returnUrl && returnUrl.startsWith('/') && !returnUrl.startsWith('//')) {
      return returnUrl;
    }

    return '/products';
  }
}
