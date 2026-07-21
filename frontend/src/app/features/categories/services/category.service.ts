import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import { Category } from '../../../shared/models/product.model';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/categories`;

  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.baseUrl);
  }
}
