import { Routes } from '@angular/router';
import { AboutPage } from './features/about/pages/about-page/about-page';
import { HomePage } from './features/home/pages/home-page/home-page';
import { OrchardPage } from './features/orchard/pages/orchard-page/orchard-page';
import { ProductPage } from './features/products/pages/product-page/product-page';
import { RecipesPage } from './features/recipes/pages/recipes-page/recipes-page';
import { MainLayout } from './layout/main-layout/main-layout';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    children: [
      { path: '', component: HomePage },
      { path: 'products', component: ProductPage },
      { path: 'orchard', component: OrchardPage },
      { path: 'recipes', component: RecipesPage },
      { path: 'about', component: AboutPage },
    ],
  },
];
