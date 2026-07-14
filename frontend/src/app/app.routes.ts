import { Routes } from '@angular/router';
import { MainLayout } from './layout/main-layout/main-layout';
import { HomePage } from './features/home/pages/home-page/home-page';

export const routes: Routes = [
   {
      path: '',
      component: MainLayout,
      children: [
        { path: '', component: HomePage },
      ],
    },
  ];
