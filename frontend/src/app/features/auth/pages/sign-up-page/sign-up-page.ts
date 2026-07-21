import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { NotificationService } from '../../../../core/services/notification.service';
import { AuthService } from '../../../../core/services/auth.service';

function passwordStrengthValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value as string;

  if (!value) {
    return null;
  }

  const isValid = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9])\S{8,30}$/.test(value);
  return isValid ? null : { passwordStrength: true };
}

function passwordsMatchValidator(group: AbstractControl): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirmPassword = group.get('confirm_password')?.value;

  if (!password || !confirmPassword) {
    return null;
  }

  return password === confirmPassword ? null : { passwordMismatch: true };
}

@Component({
  selector: 'app-sign-up-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './sign-up-page.html',
  styleUrl: './sign-up-page.scss',
})
export class SignUpPage {
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  readonly route = inject(ActivatedRoute);
  private readonly formBuilder = inject(FormBuilder);

  loading = signal(false);

  readonly form = this.formBuilder.group(
    {
      first_name: ['', Validators.required],
      last_name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      address: [''],
      password: ['', [Validators.required, passwordStrengthValidator]],
      confirm_password: ['', Validators.required],
    },
    { validators: passwordsMatchValidator },
  );

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.notificationService.error(this.getFormErrorMessage());
      return;
    }

    this.loading.set(true);
    const raw = this.form.getRawValue();

    this.authService
      .register({
        first_name: (raw.first_name ?? '').trim(),
        last_name: (raw.last_name ?? '').trim(),
        email: (raw.email ?? '').trim(),
        password: raw.password ?? '',
        address: (raw.address ?? '').trim() || undefined,
      })
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.notificationService.success('Welcome! Your account has been created.');
          this.router.navigateByUrl(this.getReturnUrl());
        },
        error: (err) => {
          this.loading.set(false);
          this.notificationService.error(this.resolveRegisterError(err));
        },
      });
  }

  private resolveRegisterError(err: { status?: number; error?: { message?: string; error?: string } }): string {
    if (err?.error?.message) {
      return err.error.message;
    }

    if (err.status === 404) {
      return 'Registration service is unavailable. Please restart the backend server and try again.';
    }

    if (err.status === 0) {
      return 'Cannot reach the server. Make sure the backend is running on port 8080.';
    }

    return err?.error?.error ?? 'Could not create your account.';
  }

  private getFormErrorMessage(): string {
    const controls = this.form.controls;

    if (controls.first_name.invalid || controls.last_name.invalid) {
      return 'First name and last name are required.';
    }

    if (controls.email.invalid) {
      return 'Please enter a valid email address.';
    }

    if (controls.password.hasError('passwordStrength')) {
      return 'Password must be 8-30 characters and include upper, lower, number, and special character.';
    }

    if (this.form.hasError('passwordMismatch')) {
      return 'Passwords do not match.';
    }

    return 'Please check the form and try again.';
  }

  private getReturnUrl(): string {
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');

    if (returnUrl && returnUrl.startsWith('/') && !returnUrl.startsWith('//')) {
      return returnUrl;
    }

    return '/products';
  }
}
