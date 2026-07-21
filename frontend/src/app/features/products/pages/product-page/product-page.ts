import { CurrencyPipe, DOCUMENT } from '@angular/common';
import { afterNextRender, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { CartService } from '../../../../core/services/cart.service';
import { ProductDetailModal } from '../../../../shared/components/product-detail-modal/product-detail-modal';
import { Product } from '../../../../shared/models/product.model';
import { getProductImageUrl } from '../../../../shared/utils/product-image.util';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-product-page',
  imports: [CurrencyPipe, ProductDetailModal],
  templateUrl: './product-page.html',
  styleUrl: './product-page.scss',
})
export class ProductPage {
  private readonly productService = inject(ProductService);
  private readonly cartService = inject(CartService);
  private readonly document = inject(DOCUMENT);
  private readonly route = inject(ActivatedRoute);

  products = signal<Product[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  activeCategory = signal('All');
  wishlist = signal<Set<string>>(new Set());
  selectedProduct = signal<Product | null>(null);
  detailLoading = signal(false);

  categories = computed(() => {
    const names = this.products()
      .map((product) => product.category?.name)
      .filter((name): name is string => Boolean(name));

    return ['All', ...Array.from(new Set(names)).sort()];
  });

  filteredProducts = computed(() => {
    const category = this.activeCategory();
    const products = this.products();

    if (category === 'All') {
      return products;
    }

    return products.filter((product) => product.category?.name === category);
  });

  constructor() {
    afterNextRender(() => this.loadProducts());
  }

  loadProducts(): void {
    this.loading.set(true);
    this.error.set(null);

    this.productService.getAllProducts().subscribe({
      next: (products) => {
        this.products.set(products);
        this.applyCategoryFromQuery();
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load products. Make sure the backend is running on port 8080.');
        this.loading.set(false);
      },
    });
  }

  setCategory(category: string): void {
    this.activeCategory.set(category);
  }

  openProduct(product: Product): void {
    this.document.body.style.overflow = 'hidden';
    this.selectedProduct.set(product);
    this.detailLoading.set(true);

    this.productService.getProductById(product.id).subscribe({
      next: (fullProduct) => {
        this.selectedProduct.set(fullProduct);
        this.updateProductInList(fullProduct);
        this.detailLoading.set(false);
      },
      error: () => {
        this.detailLoading.set(false);
      },
    });
  }

  closeProduct(): void {
    this.selectedProduct.set(null);
    this.detailLoading.set(false);
    this.document.body.style.overflow = '';
  }

  addToCart(product: Product, event?: Event): void {
    event?.stopPropagation();
    this.cartService.addProduct(product, 1);
  }

  cartQuantity(id: Product['id']): number {
    return this.cartService.getQuantity(id);
  }

  toggleWishlist(id: Product['id'], event?: Event): void {
    event?.stopPropagation();
    const key = this.productKey(id);

    this.wishlist.update((current) => {
      const next = new Set(current);

      if (next.has(key)) {
        next.delete(key);
      } else {
        next.add(key);
      }

      return next;
    });
  }

  isInCart(id: Product['id']): boolean {
    return this.cartService.isInCart(id);
  }

  isInWishlist(id: Product['id']): boolean {
    return this.wishlist().has(this.productKey(id));
  }

  productImage(product: Product, detail = false): string {
    return getProductImageUrl(product, detail);
  }

  productBadge(product: Product): string | null {
    if (product.stock_quantity === 0) {
      return 'Out of Stock';
    }

    if (product.stock_quantity <= 5) {
      return 'Low Stock';
    }

    return null;
  }

  stockLabel(product: Product): string {
    if (product.stock_quantity === 0) {
      return 'Out of stock';
    }

    return `${product.stock_quantity} in stock`;
  }

  productFlavorLabel(product: Product): string {
    return (
      product.flavor_profile?.overall_profile ??
      product.flavor_profile?.tasting_description ??
      ''
    );
  }

  productWeightLabel(product: Product): string {
    const categoryName = product.category?.name?.toLowerCase() ?? '';

    if (categoryName.includes('cider') || categoryName.includes('juice')) {
      return '750ml bottle';
    }

    if (categoryName.includes('gift')) {
      return 'gift box';
    }

    return 'each';
  }

  private applyCategoryFromQuery(): void {
    const category = this.route.snapshot.queryParamMap.get('category');

    if (!category) {
      return;
    }

    const availableCategories = this.categories();

    if (availableCategories.includes(category)) {
      this.activeCategory.set(category);
    }
  }

  private updateProductInList(updated: Product): void {
    this.products.update((current) =>
      current.map((product) => (String(product.id) === String(updated.id) ? updated : product))
    );
  }

  private productKey(id: Product['id']): string {
    return String(id);
  }
}
