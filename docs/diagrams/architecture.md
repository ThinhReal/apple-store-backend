# Grove & Root — System Architecture

> Render on GitHub, VS Code (Mermaid extension), or [mermaid.live](https://mermaid.live).

## 1. Deployment Architecture (AWS)

```mermaid
flowchart TB
    subgraph Client["Client Layer"]
        Browser["Web Browser<br/>(Customer / Admin)"]
    end

    subgraph AWS["Amazon Web Services"]
        CF["CloudFront CDN<br/>https://dilx8k8ttt2y5.cloudfront.net"]
        S3["Amazon S3<br/>Angular SPA static assets<br/>(HTML, JS, CSS, images)"]
        EC2["Amazon EC2<br/>Spring Boot API :8080"]
    end

    subgraph External["External Services"]
        Aiven["Aiven MySQL<br/>(Managed Database)"]
    end

    Browser -->|"HTTPS — Load SPA"| CF
    CF --> S3
    Browser -->|"HTTPS + CORS<br/>JWT HttpOnly Cookies"| EC2
    EC2 -->|"JDBC / Spring Data JPA"| Aiven
```

## 2. Application Architecture

```mermaid
flowchart TB
    subgraph Frontend["Angular 22 Frontend (SSR-capable SPA)"]
        direction TB

        subgraph Pages["Feature Pages"]
            Home["Home / About / Orchard / Recipes"]
            Products["Product Catalog"]
            Cart["Cart (localStorage)"]
            Checkout["Checkout Page"]
            Orders["My Orders"]
            Auth["Login / Signup"]
            Admin["Admin Products / Orders"]
        end

        subgraph Core["Core Layer"]
            Guards["Guards<br/>customer / admin / checkout"]
            Interceptor["Credentials Interceptor<br/>(withCredentials: true)"]
            AuthSvc["AuthService"]
            CartSvc["CartService"]
            OrderSvc["OrderService"]
            ProductSvc["ProductService"]
        end

        subgraph Shared["Shared Components"]
            Navbar["Navbar + Cart"]
            ProductCard["Product Card / Modal"]
            Toast["Toast / Dialog"]
        end

        Pages --> Core
        Pages --> Shared
        Core --> Interceptor
    end

    subgraph Backend["Spring Boot 4 Backend"]
        direction TB

        subgraph Security["Security Layer"]
            JWTFilter["JwtAuthenticationFilter"]
            SecConfig["SecurityConfig<br/>(RBAC + CORS)"]
            JwtSvc["JwtService"]
            CookieSvc["CookieService"]
        end

        subgraph Controllers["REST Controllers"]
            AuthCtrl["AuthController<br/>/api/v1/auth/**"]
            ProductCtrl["ProductController<br/>/api/v1/products/**"]
            CategoryCtrl["CategoryController<br/>/api/v1/categories/**"]
            CheckoutCtrl["CheckoutController<br/>/api/v1/orders/me|checkout"]
            AdminOrderCtrl["AdminOrderController<br/>/api/v1/admin/orders/**"]
            UserCtrl["UserController"]
            OrderCtrl["OrderController"]
            OrderItemCtrl["OrderItemController"]
        end

        subgraph Services["Business Services"]
            AuthService["AuthService"]
            ProductService["ProductService"]
            CategoryService["CategoryService"]
            OrderService["OrderServiceImpl<br/>(@Transactional)"]
            InventoryService["InventoryServiceImpl<br/>(Optimistic Lock)"]
            UserService["UserService"]
        end

        subgraph Persistence["Persistence Layer"]
            Repos["Spring Data JPA Repositories"]
            Entities["JPA Entities"]
            Scheduler["RefreshTokenCleanupScheduler"]
        end

        subgraph CrossCutting["Cross-cutting"]
            ExceptionHandler["GlobalExceptionHandler<br/>(409 on lock conflict)"]
            OpenAPI["OpenAPI Spec + Swagger UI"]
            ModelMapper["ModelMapper DTO mapping"]
        end

        Controllers --> Security
        Controllers --> Services
        Services --> Repos
        Repos --> Entities
    end

    subgraph Database["MySQL (Aiven)"]
        DB[(Relational DB)]
    end

    Frontend -->|"REST /api/v1/*<br/>Dev: proxy → :8080"| Backend
    Backend --> DB
```

## 3. Authentication Flow

```mermaid
sequenceDiagram
    participant U as User (Browser)
    participant A as Angular AuthService
    participant API as AuthController
    participant AS as AuthService
    participant JWT as JwtService
    participant DB as MySQL

    U->>A: Login / Register
    A->>API: POST /api/v1/auth/login|register
    API->>AS: authenticate / create user
    AS->>DB: verify user / save user
    AS->>JWT: generate access + refresh tokens
    AS->>DB: persist RefreshTokenEntity
    AS-->>API: Set HttpOnly cookies
    API-->>A: AuthResponse (userId, email, role)
    A-->>U: Update currentUser signal

    Note over U,DB: Subsequent requests
    U->>API: Request + JWT cookie
    API->>JWT: JwtAuthenticationFilter reads cookie
    JWT-->>API: SecurityContext (CUSTOMER / ADMIN)
    API->>API: hasRole authorization check
```

## 4. Checkout Flow (ACID + Optimistic Locking)

```mermaid
sequenceDiagram
    participant U as Customer
    participant Cart as CartService<br/>(localStorage)
    participant API as CheckoutController
    participant OS as OrderServiceImpl<br/>(@Transactional)
    participant IS as InventoryService
    participant DB as MySQL

    U->>Cart: Add products to cart
    U->>API: POST /api/v1/orders/checkout
    API->>OS: createOrderForUser(userId, items)

    OS->>OS: validateOrderItems()
    OS->>DB: load UserEntity
    OS->>OS: merge quantities by productId
    OS->>OS: initializePendingOrder (status=PENDING)

    loop Each product
        OS->>IS: reserveStock(productId, qty)
        IS->>DB: load ProductEntity (@Version)
        IS->>IS: stockQuantity -= qty
        IS->>DB: save (optimistic lock check)
        alt Version conflict
            DB-->>IS: ObjectOptimisticLockingFailureException
            IS-->>API: HTTP 409 Conflict
        end
        OS->>OS: build OrderItemEntity
    end

    OS->>DB: save OrderEntity + OrderItems
    OS-->>API: ResponseOrderDTO
    API-->>U: 201 Created
    U->>Cart: Clear cart
```

## 5. Frontend Routes

```mermaid
graph TD
    App["app.routes.ts"]

    App --> MainLayout

    MainLayout --> Home["/"]
    MainLayout --> Products["/products"]
    MainLayout --> Checkout["/checkout<br/>checkoutGuard"]
    MainLayout --> MyOrders["/orders<br/>customerGuard"]
    MainLayout --> AuthPages["/login, /signup"]
    MainLayout --> Static["/about, /orchard, /recipes"]
    MainLayout --> AdminLogin["/admin/login"]
    MainLayout --> AdminProducts["/admin/products<br/>adminGuard"]
    MainLayout --> AdminOrders["/admin/orders<br/>adminGuard"]

    CartSvc["CartService<br/>localStorage: applestore.cart"]
    Products --> CartSvc
    Checkout --> CartSvc
    Checkout -->|"POST /orders/checkout"| API["Backend API"]
```

## 6. Role-Based Access Control

| Role | Permissions |
|------|-------------|
| `CUSTOMER` | Checkout, view personal orders (`GET /api/v1/orders/me`) |
| `ADMIN` | Product CRUD, manage order statuses |
| Public | GET products/categories, auth endpoints |
