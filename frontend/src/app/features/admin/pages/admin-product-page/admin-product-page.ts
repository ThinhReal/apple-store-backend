import { CurrencyPipe, isPlatformBrowser } from '@angular/common';
import { Component, DestroyRef, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs/operators';

import {
  AdminProductCreateDraft,
  AdminProductDraftStorage,
  isAdminProductCreateDraftEmpty,
  toAdminProductCreateDraft,
} from '../../../../core/services/admin-product-draft.storage';
import { AuthService } from '../../../../core/services/auth.service';
import { DialogService } from '../../../../core/services/dialog.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { CategoryService } from '../../../categories/services/category.service';
import { ProductService } from '../../../products/services/product.service';
import { Category, Product, ProductRequest } from '../../../../shared/models/product.model';

@Component({
  selector: 'app-admin-product-page',
  imports: [CurrencyPipe, ReactiveFormsModule, RouterLink],
  templateUrl: './admin-product-page.html',
  styleUrl: './admin-product-page.scss',
})
export class AdminProductPage implements OnInit {
  private readonly productService = inject(ProductService);
  private readonly categoryService = inject(CategoryService);
  private readonly authService = inject(AuthService);
  private readonly dialogService = inject(DialogService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly destroyRef = inject(DestroyRef);
  private readonly draftStorage = inject(AdminProductDraftStorage);

  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  loading = signal(true);
  saving = signal(false);
  editingProductId = signal<string | null>(null);

  readonly form = this.formBuilder.group({
    category_id: ['', Validators.required],
    name: ['', Validators.required],
    description: [''],
    price: [null as number | null, [Validators.required, Validators.min(0.01)]],
    stock_quantity: [null as number | null, [Validators.required, Validators.min(0)]],
    origin: [''],
    season: [''],
    image_url: [''],
    tasting_notes: [''],
    best_for: [''],
    flavor_profile: this.formBuilder.group({
      sweetness_level: [null as number | null],
      tartness_level: [null as number | null],
      overall_profile: [''],
      dominant_notes: [''],
      tasting_description: [''],
    }),
  });

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      this.loading.set(false);
      return;
    }

    this.loadData();
    this.setupCreateDraftPersistence();
  }

  private setupCreateDraftPersistence(): void {
    this.form.valueChanges
      .pipe(debounceTime(400), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.persistCreateDraft());
  }

  loadData(): void {
    this.loading.set(true);

    this.categoryService.getAllCategories().subscribe({
      next: (categories) => {
        this.categories.set(categories);

        if (this.editingProductId()) {
          return;
        }

        const restored = this.restoreCreateDraft(categories);
        if (!restored && categories.length > 0) {
          this.form.patchValue({ category_id: String(categories[0].id) });
        }
      },
      error: () => this.notificationService.error('Could not load categories.'),
    });

    this.productService.getAllProducts().subscribe({
      next: (products) => {
        this.products.set(products);
        this.loading.set(false);
      },
      error: () => {
        this.notificationService.error('Could not load products.');
        this.loading.set(false);
      },
    });
  }

  startCreate(): void {
    this.editingProductId.set(null);
    this.draftStorage.clear();
    this.form.reset({
      category_id: this.categories()[0] ? String(this.categories()[0].id) : '',
      name: '',
      description: '',
      price: null,
      stock_quantity: null,
      origin: '',
      season: '',
      image_url: '',
      tasting_notes: '',
      best_for: '',
      flavor_profile: {
        sweetness_level: null,
        tartness_level: null,
        overall_profile: '',
        dominant_notes: '',
        tasting_description: '',
      },
    });
  }

  startEdit(product: Product): void {
    this.editingProductId.set(String(product.id));
    this.form.patchValue({
      category_id: String(product.category?.id ?? ''),
      name: product.name,
      description: product.description ?? '',
      price: product.price,
      stock_quantity: product.stock_quantity,
      origin: product.origin ?? '',
      season: product.season ?? '',
      image_url: product.image_url ?? '',
      tasting_notes: (product.tasting_notes ?? []).join(', '),
      best_for: (product.best_for ?? []).join(', '),
      flavor_profile: {
        sweetness_level: product.flavor_profile?.sweetness_level ?? null,
        tartness_level: product.flavor_profile?.tartness_level ?? null,
        overall_profile: product.flavor_profile?.overall_profile ?? '',
        dominant_notes: (product.flavor_profile?.dominant_notes ?? []).join(', '),
        tasting_description: product.flavor_profile?.tasting_description ?? '',
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.notificationService.error(this.getFormErrorMessage());
      return;
    }

    this.saving.set(true);

    const payload = this.buildPayload();
    const editingId = this.editingProductId();

    const request$ = editingId
      ? this.productService.updateProduct(editingId, payload)
      : this.productService.createProduct(payload);

    request$.subscribe({
      next: () => {
        this.saving.set(false);
        this.notificationService.success(
          editingId ? 'Product updated successfully.' : 'Product created successfully.',
        );
        this.startCreate();
        this.loadData();
      },
      error: (err) => {
        this.saving.set(false);
        this.notificationService.error(err?.error?.message ?? 'Could not save product.');
      },
    });
  }

  async deleteProduct(product: Product): Promise<void> {
    const confirmed = await this.dialogService.confirm({
      title: 'Delete product',
      message: `Delete "${product.name}"? This action cannot be undone.`,
      confirmLabel: 'Delete',
      cancelLabel: 'Cancel',
      variant: 'danger',
    });

    if (!confirmed) {
      return;
    }

    this.productService.deleteProduct(product.id).subscribe({
      next: () => {
        this.notificationService.success(`Deleted "${product.name}".`);
        if (this.editingProductId() === String(product.id)) {
          this.startCreate();
        }
        this.loadData();
      },
      error: (err) => {
        this.notificationService.error(err?.error?.message ?? 'Could not delete product.');
      },
    });
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/admin/login']),
    });
  }

  private persistCreateDraft(): void {
    if (this.editingProductId()) {
      return;
    }

    const draft = toAdminProductCreateDraft(this.form.getRawValue() as AdminProductCreateDraft);

    if (isAdminProductCreateDraftEmpty(draft)) {
      this.draftStorage.clear();
      return;
    }

    this.draftStorage.save(draft);
  }

  private restoreCreateDraft(categories: Category[]): boolean {
    const savedDraft = this.draftStorage.load();
    if (!savedDraft) {
      return false;
    }

    const draft = toAdminProductCreateDraft(savedDraft);
    const categoryExists = categories.some((category) => String(category.id) === draft.category_id);

    this.form.patchValue({
      ...draft,
      category_id: categoryExists ? draft.category_id : categories[0] ? String(categories[0].id) : '',
    });

    this.notificationService.info('Restored your unsaved product draft.');
    return true;
  }

  private buildPayload(): ProductRequest {
    const raw = this.form.getRawValue();

    return {
      category_id: raw.category_id ?? '',
      name: (raw.name ?? '').trim(),
      description: (raw.description ?? '').trim() || undefined,
      price: Number(raw.price),
      stock_quantity: Number(raw.stock_quantity),
      origin: (raw.origin ?? '').trim() || undefined,
      season: (raw.season ?? '').trim() || undefined,
      image_url: (raw.image_url ?? '').trim() || undefined,
      tasting_notes: this.splitList(raw.tasting_notes ?? ''),
      best_for: this.splitList(raw.best_for ?? ''),
      flavor_profile: this.buildFlavorProfile({
        sweetness_level: raw.flavor_profile.sweetness_level,
        tartness_level: raw.flavor_profile.tartness_level,
        overall_profile: raw.flavor_profile.overall_profile ?? '',
        dominant_notes: raw.flavor_profile.dominant_notes ?? '',
        tasting_description: raw.flavor_profile.tasting_description ?? '',
      }),
    };
  }

  private buildFlavorProfile(
    raw: {
      sweetness_level: number | null;
      tartness_level: number | null;
      overall_profile: string;
      dominant_notes: string;
      tasting_description: string;
    },
  ): ProductRequest['flavor_profile'] {
    const overallProfile = raw.overall_profile.trim();
    const tastingDescription = raw.tasting_description.trim();
    const dominantNotes = this.splitList(raw.dominant_notes);
    const sweetnessLevel =
      raw.sweetness_level === null || raw.sweetness_level === undefined
        ? undefined
        : Number(raw.sweetness_level);
    const tartnessLevel =
      raw.tartness_level === null || raw.tartness_level === undefined
        ? undefined
        : Number(raw.tartness_level);

    const hasValues =
      overallProfile ||
      tastingDescription ||
      dominantNotes ||
      sweetnessLevel !== undefined ||
      tartnessLevel !== undefined;

    if (!hasValues) {
      return undefined;
    }

    return {
      overall_profile: overallProfile || undefined,
      tasting_description: tastingDescription || undefined,
      dominant_notes: dominantNotes,
      sweetness_level: sweetnessLevel,
      tartness_level: tartnessLevel,
    };
  }

  private getFormErrorMessage(): string {
    const controls = this.form.controls;

    if (controls.category_id.invalid) {
      return 'Please select a category.';
    }

    if (controls.name.invalid) {
      return 'Product name is required.';
    }

    if (controls.price.hasError('required')) {
      return 'Price is required.';
    }

    if (controls.price.hasError('min')) {
      return 'Price must be greater than 0.';
    }

    if (controls.stock_quantity.hasError('required')) {
      return 'Stock quantity is required.';
    }

    return 'Please check the form and try again.';
  }

  private splitList(value: string): string[] | undefined {
    const items = value
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean);

    return items.length > 0 ? items : undefined;
  }
}
