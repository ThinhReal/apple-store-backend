import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-root',
  standalone: true,
  template: `
    <div class="min-h-screen bg-gray-50 font-sans selection:bg-blue-200">
      <!-- Navbar (Thanh điều hướng) -->
      <header class="bg-black text-white py-4 px-6 md:px-12 flex justify-between items-center shadow-lg sticky top-0 z-50">
        <div class="flex items-center gap-2 cursor-pointer hover:opacity-80 transition-opacity">
          <span class="text-2xl">🍎</span>
          <h1 class="text-xl font-semibold tracking-wide">Applestore</h1>
        </div>
        <nav class="hidden md:block">
          <ul class="flex space-x-8 text-xs font-medium tracking-widest uppercase">
            <li class="hover:text-gray-400 cursor-pointer transition-colors">Store</li>
            <li class="hover:text-gray-400 cursor-pointer transition-colors">Mac</li>
            <li class="hover:text-gray-400 cursor-pointer transition-colors">iPad</li>
            <li class="hover:text-gray-400 cursor-pointer transition-colors">iPhone</li>
          </ul>
        </nav>
        <div class="flex items-center gap-4">
          <span class="cursor-pointer text-xl hover:text-gray-400 transition-colors">🛒</span>
        </div>
      </header>

      <!-- Main Content (Nội dung chính) -->
      <main class="max-w-7xl mx-auto py-16 px-4 sm:px-6 lg:px-8">

        <!-- Hero Section -->
        <div class="text-center mb-16 animate-fade-in-up">
          <h2 class="text-5xl font-extrabold text-gray-900 tracking-tight sm:text-6xl mb-4">
            Welcome to the <span class="text-blue-600">Apple Store</span>
          </h2>
          <p class="mt-4 text-xl text-gray-500 max-w-2xl mx-auto">
            Khám phá những sản phẩm mới nhất. Thiết kế tuyệt đẹp, hiệu năng đỉnh cao.
          </p>
        </div>

        <!-- Product Grid (Danh sách sản phẩm dùng @for) -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
          @for (product of products(); track product.id) {
            <div class="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-xl hover:-translate-y-1 transition-all duration-300 flex flex-col p-8 group">

              <!-- Icon/Hình ảnh giả lập -->
              <div class="h-56 w-full bg-gray-50 rounded-2xl mb-8 flex items-center justify-center text-8xl group-hover:scale-105 transition-transform duration-500">
                {{ product.icon }}
              </div>

              <!-- Thông tin sản phẩm -->
              <div class="text-center flex-grow flex flex-col">
                <h3 class="text-2xl font-bold text-gray-900 mb-3">{{ product.name }}</h3>
                <p class="text-gray-500 text-sm mb-6 flex-grow leading-relaxed">{{ product.description }}</p>
                <div class="text-xl font-semibold text-gray-900 mt-auto mb-6">
                  Từ \${{ product.price }}
                </div>

                <!-- Nút bấm -->
                <div class="flex gap-3 justify-center">
                  <button class="bg-blue-600 text-white font-medium py-2.5 px-6 rounded-full hover:bg-blue-700 transition-colors shadow-md hover:shadow-lg text-sm">
                    Mua ngay
                  </button>
                  <button class="bg-gray-100 text-gray-900 font-medium py-2.5 px-6 rounded-full hover:bg-gray-200 transition-colors text-sm">
                    Tìm hiểu thêm
                  </button>
                </div>
              </div>

            </div>
          }
        </div>
      </main>

      <!-- Footer -->
      <footer class="bg-white border-t border-gray-200 py-8 mt-12 text-center text-sm text-gray-500">
        <p>© 2026 Applestore Demo. All rights reserved.</p>
      </footer>
    </div>
  `,
  styles: [`
    /* Bạn có thể viết CSS thuần ở đây nếu Tailwind không đủ đáp ứng,
       nhưng ở ví dụ này Tailwind đã làm hết mọi việc. */
    .animate-fade-in-up {
      animation: fadeInUp 0.8s ease-out forwards;
    }
    @keyframes fadeInUp {
      from { opacity: 0; transform: translateY(20px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
})
export class App {
  // Dùng signal() để lưu trữ State. Thay thế cho mảng (array) bình thường.
  // Khi dữ liệu trong signal thay đổi, giao diện sẽ tự động cập nhật ngay lập tức.
  products = signal([
    {
      id: 1,
      name: 'iPhone 16 Pro',
      description: 'Titanium tuyệt đẹp. Bền bỉ và nhẹ nhàng. Chip A18 Pro mạnh mẽ.',
      price: 999,
      icon: '📱'
    },
    {
      id: 2,
      name: 'MacBook Pro M3',
      description: 'Hiệu năng đột phá. Đồ họa xuất sắc. Thời lượng pin lên đến 22 giờ.',
      price: 1599,
      icon: '💻'
    },
    {
      id: 3,
      name: 'Apple Watch Ultra 2',
      description: 'Đồng hồ thể thao thông minh nhất. GPS băng tần kép, màn hình siêu sáng.',
      price: 799,
      icon: '⌚'
    }
  ]);
}
