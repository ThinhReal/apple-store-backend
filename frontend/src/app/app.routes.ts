import { Routes } from '@angular/router';
import { AboutPage } from './features/about/pages/about-page/about-page';
import { AdminLoginPage } from './features/admin/pages/admin-login-page/admin-login-page';
import { AdminOrderPage } from './features/admin/pages/admin-order-page/admin-order-page';
import { AdminProductPage } from './features/admin/pages/admin-product-page/admin-product-page';
import { LoginPage } from './features/auth/pages/login-page/login-page';
import { SignUpPage } from './features/auth/pages/sign-up-page/sign-up-page';
import { CheckoutPage } from './features/checkout/pages/checkout-page/checkout-page';
import { HomePage } from './features/home/pages/home-page/home-page';
import { MyOrdersPage } from './features/orders/pages/my-orders-page/my-orders-page';
import { OrchardPage } from './features/orchard/pages/orchard-page/orchard-page';
import { ProductPage } from './features/products/pages/product-page/product-page';
import { RecipesPage } from './features/recipes/pages/recipes-page/recipes-page';
import { MainLayout } from './layout/main-layout/main-layout';
import { adminGuard } from './core/guards/admin.guard';
import { checkoutGuard } from './core/guards/checkout.guard';
import { customerGuard } from './core/guards/customer.guard';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    children: [
      { path: '', component: HomePage },
      { path: 'products', component: ProductPage },
      { path: 'checkout', component: CheckoutPage, canActivate: [checkoutGuard] },
      { path: 'orders', component: MyOrdersPage, canActivate: [customerGuard] },
      { path: 'orchard', component: OrchardPage },
      { path: 'recipes', component: RecipesPage },
      { path: 'about', component: AboutPage },
      { path: 'login', component: LoginPage },
      { path: 'signup', component: SignUpPage },
      { path: 'admin/login', component: AdminLoginPage },
      { path: 'admin/products', component: AdminProductPage, canActivate: [adminGuard] },
      { path: 'admin/orders', component: AdminOrderPage, canActivate: [adminGuard] },
    ],
  },
];
