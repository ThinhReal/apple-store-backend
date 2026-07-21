import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { catchError, map, Observable, of, tap } from 'rxjs';

import { API_BASE_URL } from '../config/api.config';
import { AuthUser, LoginRequest, RegisterRequest } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/auth`;

  readonly currentUser = signal<AuthUser | null>(null);
  readonly authChecked = signal(false);

  login(credentials: LoginRequest): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${this.baseUrl}/login`, credentials).pipe(
      tap((user) => this.currentUser.set(user))
    );
  }

  register(payload: RegisterRequest): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${this.baseUrl}/register`, payload).pipe(
      tap((user) => this.currentUser.set(user))
    );
  }

  logout(): Observable<void> {
    return this.http.post<AuthUser>(`${this.baseUrl}/logout`, {}).pipe(
      tap(() => this.currentUser.set(null)),
      map(() => undefined)
    );
  }

  loadCurrentUser(): Observable<AuthUser | null> {
    return this.http.get<AuthUser>(`${this.baseUrl}/me`).pipe(
      tap((user) => {
        this.currentUser.set(user);
        this.authChecked.set(true);
      }),
      catchError(() => {
        this.currentUser.set(null);
        this.authChecked.set(true);
        return of(null);
      })
    );
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'ADMIN';
  }

  isCustomer(): boolean {
    return this.currentUser()?.role === 'CUSTOMER';
  }

  isAuthenticated(): boolean {
    return this.currentUser()?.role === 'ADMIN' || this.currentUser()?.role === 'CUSTOMER';
  }
}
