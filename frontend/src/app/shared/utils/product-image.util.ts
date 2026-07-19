import { Product } from '../models/product.model';

const DEFAULT_CARD_IMAGE =
  'https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=500&h=500&fit=crop&auto=format';

const DEFAULT_DETAIL_IMAGE =
  'https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=900&h=900&fit=crop&auto=format';

const CIDER_IMAGE =
  'https://images.unsplash.com/photo-1535914254981-b5012eebbd15?w=500&h=500&fit=crop&auto=format';

const CIDER_DETAIL_IMAGE =
  'https://images.unsplash.com/photo-1535914254981-b5012eebbd15?w=900&h=900&fit=crop&auto=format';

const GIFT_IMAGE =
  'https://images.unsplash.com/photo-1510627489930-0c1b0bfb6785?w=500&h=500&fit=crop&auto=format';

const GIFT_DETAIL_IMAGE =
  'https://images.unsplash.com/photo-1510627489930-0c1b0bfb6785?w=900&h=900&fit=crop&auto=format';

export function getProductImageUrl(product: Product, detail = false): string {
  if (product.image_url?.trim()) {
    return product.image_url.trim();
  }

  const categoryName = product.category?.name?.toLowerCase() ?? '';

  if (categoryName.includes('cider') || categoryName.includes('juice')) {
    return detail ? CIDER_DETAIL_IMAGE : CIDER_IMAGE;
  }

  if (categoryName.includes('gift')) {
    return detail ? GIFT_DETAIL_IMAGE : GIFT_IMAGE;
  }

  return detail ? DEFAULT_DETAIL_IMAGE : DEFAULT_CARD_IMAGE;
}
