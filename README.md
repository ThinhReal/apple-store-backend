# Grove & Root - Apple Store E-commerce

A full-stack e-commerce application for an artisan apple orchard storefront. Customers can browse products, manage a cart, and checkout securely. Admins can manage the product catalog and update customer order statuses.

The backend is designed around **ACID database guarantees**, especially at checkout, where inventory, orders, and order items must stay consistent even under concurrent purchases.

## Tech Stack

### Backend
| Technology | Purpose |
| :--- | :--- |
| **Java 21** | Application runtime |
| **Spring Boot 4** | REST API, dependency injection, configuration |
| **Spring Data JPA / Hibernate** | ORM, entity mapping, transactions |
| **MySQL** | Primary relational database (Aiven Cloud) |
| **Spring Security** | Authentication and role-based authorization |
| **JWT (JJWT)** | Stateless access tokens via HTTP-only cookies |
| **SpringDoc OpenAPI** | Interactive API documentation (Swagger UI) |
| **OpenAPI Generator** | API contract-first code generation |
| **Lombok** | Boilerplate reduction for entities and DTOs |
| **Maven** | Build and dependency management |

### Frontend
| Technology | Purpose |
| :--- | :--- |
| **Angular 22** | SPA framework with standalone components |
| **TypeScript** | Type-safe UI development |
| **RxJS** | Async data flows for API calls |
| **SCSS** | Component styling |
| **Angular SSR** | Server-side rendering support |

## ACID in This Application

Checkout is the most sensitive workflow: multiple products, stock deduction, order creation, and order line items must succeed or fail together.

### Atomicity
Checkout runs inside a single `@Transactional` service method. If any step fails (invalid product, insufficient stock, persistence error), the entire operation rolls back — no partial orders and no half-updated inventory.

### Consistency
Business rules are enforced before writes:
- Order quantity must be greater than zero
- Stock cannot go negative
- Only valid order status transitions are allowed for admin updates
- Cancelled orders restore inventory; reactivating a cancelled order reserves stock again

### Isolation
Concurrent checkouts for the same product are handled with **optimistic locking** (see below). Each transaction reads product stock with a version number and commits only if no other transaction has modified that product in the meantime.

### Durability
Committed orders and inventory changes are persisted to MySQL. Once checkout completes, the order and updated stock survive application restarts.

## Optimistic Locking

To prevent overselling when two customers checkout the same product at the same time, the app uses JPA optimistic locking on `ProductEntity`:

```java
@Version
@Column(nullable = false)
private Long version = 0L;
```

### How it works
1. When a product is loaded, Hibernate reads its current `version`.
2. During checkout, `InventoryService` deducts stock and saves the product.
3. On save, Hibernate checks that the `version` in the database still matches what was read.
4. If another checkout updated the product first, Hibernate throws `ObjectOptimisticLockingFailureException`.
5. A global exception handler converts this to **HTTP 409 Conflict** with a user-friendly message:

   > *"The product stock has been updated by another user. Please review your cart."*

The checkout page displays this message so the customer can refresh their cart and try again.

### Why optimistic locking?
- **No long-held database locks** — better throughput for read-heavy catalog traffic
- **Safe under contention** — conflicts are detected at commit time rather than silently overselling
- **Fits e-commerce patterns** — stock conflicts are rare but must be handled correctly when they occur

## Features

- **Customer**: product catalog, cart (localStorage), signup/login, checkout, order history
- **Admin**: product CRUD, order management with status updates (`PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`)
- **Security**: JWT cookies, role-based access (`CUSTOMER`, `ADMIN`), CORS for local development

## Getting Started

### Requirements
- Java 21+
- Maven 3.x
- Node.js 20+ and npm
- MySQL database (local or [Aiven](https://aiven.io/))

### Environment Variables

| Variable | Description |
| :--- | :--- |
| `MYSQL_URL` | JDBC connection URL |
| `PASSWORD` | Database password |
| `PORT` | Backend server port (e.g. `8080`) |

Example:

```bash
export MYSQL_URL="jdbc:mysql://localhost:3306/applestore"
export PASSWORD="your_password"
export PORT=8080
```

### Run the backend

```bash
./mvnw spring-boot:run
```

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Run the frontend

```bash
cd frontend
npm install
npm start
```

App: [http://localhost:4200](http://localhost:4200)

The dev server proxies `/api` requests to the backend on port 8080.

## Project Structure

```
applestore/
├── src/main/java/          # Spring Boot backend (controllers, services, entities)
├── src/main/resources/     # Application config and OpenAPI spec
├── frontend/src/app/       # Angular application
└── pom.xml                 # Backend dependencies
```

## Author

**Thịnh Nguyen** - Backend Developer
