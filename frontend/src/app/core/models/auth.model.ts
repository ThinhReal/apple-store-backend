export type UserRole = 'ADMIN' | 'CUSTOMER';

export interface AuthUser {
  userId?: number;
  email?: string;
  role?: UserRole;
  message?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  first_name: string;
  last_name: string;
  address?: string;
}
