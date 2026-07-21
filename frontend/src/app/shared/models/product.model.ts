export interface Category {
  id: string | number;
  name: string;
  description?: string;
}

export interface FlavorProfile {
  sweetness_level?: number;
  tartness_level?: number;
  overall_profile?: string;
  dominant_notes?: string[];
  tasting_description?: string;
}

export interface Product {
  id: string | number;
  name: string;
  description?: string;
  price: number;
  stock_quantity: number;
  category?: Category;
  tasting_notes?: string[];
  best_for?: string[];
  origin?: string;
  season?: string;
  image_url?: string;
  flavor_profile?: FlavorProfile;
  created_at?: string;
}

export interface ProductRequest {
  category_id: string | number;
  name: string;
  description?: string;
  price: number;
  stock_quantity: number;
  tasting_notes?: string[];
  best_for?: string[];
  origin?: string;
  season?: string;
  image_url?: string;
  flavor_profile?: FlavorProfile;
}
