import { isPlatformBrowser } from '@angular/common';
import { inject, Injectable, PLATFORM_ID } from '@angular/core';

const STORAGE_KEY = 'applestore.admin-product.create-draft';

export interface AdminProductCreateDraft {
  category_id: string;
  name: string;
  description: string;
  price: number | null;
  stock_quantity: number | null;
  origin: string;
  season: string;
  image_url: string;
  tasting_notes: string;
  best_for: string;
  flavor_profile: {
    sweetness_level: number | null;
    tartness_level: number | null;
    overall_profile: string;
    dominant_notes: string;
    tasting_description: string;
  };
}

@Injectable({ providedIn: 'root' })
export class AdminProductDraftStorage {
  private readonly platformId = inject(PLATFORM_ID);

  save(draft: AdminProductCreateDraft): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    localStorage.setItem(STORAGE_KEY, JSON.stringify(draft));
  }

  load(): AdminProductCreateDraft | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) {
        return null;
      }

      return JSON.parse(raw) as AdminProductCreateDraft;
    } catch {
      return null;
    }
  }

  clear(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    localStorage.removeItem(STORAGE_KEY);
  }
}

export function isAdminProductCreateDraftEmpty(draft: AdminProductCreateDraft): boolean {
  const flavor = draft.flavor_profile;

  return (
    !draft.name.trim() &&
    !draft.description.trim() &&
    draft.price === null &&
    draft.stock_quantity === null &&
    !draft.origin.trim() &&
    !draft.season.trim() &&
    !draft.image_url.trim() &&
    !draft.tasting_notes.trim() &&
    !draft.best_for.trim() &&
    flavor.sweetness_level === null &&
    flavor.tartness_level === null &&
    !flavor.overall_profile.trim() &&
    !flavor.dominant_notes.trim() &&
    !flavor.tasting_description.trim()
  );
}

export function toAdminProductCreateDraft(
  raw: AdminProductCreateDraft,
): AdminProductCreateDraft {
  return {
    category_id: raw.category_id ?? '',
    name: raw.name ?? '',
    description: raw.description ?? '',
    price: raw.price ?? null,
    stock_quantity: raw.stock_quantity ?? null,
    origin: raw.origin ?? '',
    season: raw.season ?? '',
    image_url: raw.image_url ?? '',
    tasting_notes: raw.tasting_notes ?? '',
    best_for: raw.best_for ?? '',
    flavor_profile: {
      sweetness_level: raw.flavor_profile?.sweetness_level ?? null,
      tartness_level: raw.flavor_profile?.tartness_level ?? null,
      overall_profile: raw.flavor_profile?.overall_profile ?? '',
      dominant_notes: raw.flavor_profile?.dominant_notes ?? '',
      tasting_description: raw.flavor_profile?.tasting_description ?? '',
    },
  };
}
