# Grove & Root — Entity Relationship Diagram (ERD)

> Render on GitHub, VS Code (Mermaid extension), or [mermaid.live](https://mermaid.live).

## ERD (Full Schema)

```mermaid
erDiagram
    USERS {
        bigint id PK
        varchar email UK "unique"
        varchar password
        varchar first_name
        varchar last_name
        varchar address
        enum role "CUSTOMER | ADMIN"
    }

    REFRESH_TOKENS {
        bigint id PK
        bigint user_id FK
        varchar token_id UK "unique, 64 chars"
        varchar token_hash "128 chars"
        timestamp expiry_date
        boolean is_revoked
    }

    CATEGORY_ENTITY {
        bigint id PK
        varchar name UK "unique"
        varchar description
    }

    PRODUCTS {
        bigint id PK
        bigint category_id FK
        varchar category "denormalized name"
        varchar name UK "unique"
        text description
        varchar origin
        varchar season
        json flavor_profile "FlavorProfileValue"
        decimal price
        int stock_quantity
        bigint version "optimistic lock"
        varchar image_url
        timestamp created_at
    }

    PRODUCT_TASTING_NOTES {
        bigint product_id FK
        int note_order
        varchar note
    }

    PRODUCT_BEST_FOR {
        bigint product_id FK
        int use_case_order
        varchar use_case
    }

    ORDERS {
        bigint id PK
        bigint user_id FK
        enum status "PENDING|PROCESSING|SHIPPED|DELIVERED|CANCELLED"
        timestamp order_date
        double total_amount
    }

    ORDER_ITEM {
        bigint id PK
        bigint order_id FK
        bigint product_id FK
        int quantity
        double unit_price
    }

    USERS ||--o{ ORDERS : "places"
    USERS ||--o{ REFRESH_TOKENS : "has"
    CATEGORY_ENTITY ||--o{ PRODUCTS : "contains"
    ORDERS ||--|{ ORDER_ITEM : "includes"
    PRODUCTS ||--o{ ORDER_ITEM : "referenced_in"
    PRODUCTS ||--|{ PRODUCT_TASTING_NOTES : "has"
    PRODUCTS ||--|{ PRODUCT_BEST_FOR : "has"
```

## Entity Relationships (JPA Mapping)

```mermaid
graph LR
    subgraph Auth["Auth Domain"]
        U["UserEntity<br/>table: users"]
        RT["RefreshTokenEntity<br/>table: refresh_tokens"]
    end

    subgraph Catalog["Catalog Domain"]
        C["CategoryEntity"]
        P["ProductEntity<br/>table: products"]
        TN["product_tasting_notes"]
        BF["product_best_for"]
    end

    subgraph Order["Order Domain"]
        O["OrderEntity<br/>table: orders"]
        OI["OrderItemEntity<br/>table: OrderItem"]
    end

    U -->|"1:N mappedBy=user"| O
    U -->|"1:N FK user_id"| RT
    C -->|"1:N mappedBy=category"| P
    P -->|"ElementCollection"| TN
    P -->|"ElementCollection"| BF
    O -->|"1:N mappedBy=order cascade=ALL"| OI
    P -->|"1:N mappedBy=product"| OI
    O -->|"N:1 FK user_id"| U
    OI -->|"N:1 FK order_id"| O
    OI -->|"N:1 FK product_id"| P
    P -->|"N:1 FK category_id"| C
```

## Relationship Summary

| Entity A | Relationship | Entity B | Cardinality | Notes |
|----------|--------------|----------|-------------|-------|
| `UserEntity` | places | `OrderEntity` | 1 : N | FK `user_id` on `orders` |
| `UserEntity` | owns | `RefreshTokenEntity` | 1 : N | JWT refresh token rotation |
| `CategoryEntity` | contains | `ProductEntity` | 1 : N | FK `category_id`; denormalized `categoryName` |
| `OrderEntity` | includes | `OrderItemEntity` | 1 : N | `CascadeType.ALL` |
| `ProductEntity` | referenced in | `OrderItemEntity` | 1 : N | Snapshot `unit_price` at order time |
| `ProductEntity` | has | `product_tasting_notes` | 1 : N | `@ElementCollection` |
| `ProductEntity` | has | `product_best_for` | 1 : N | `@ElementCollection` |

## Enums

**UserRole:** `ADMIN`, `CUSTOMER`

**OrderStatus:** `PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`

### Order Status Side Effects

- Transition **to** `CANCELLED`: restore inventory (`releaseStock`)
- Transition **from** `CANCELLED` to active: reserve inventory again (`reserveStock`)

## Source Files

| Entity | Java Class |
|--------|------------|
| User | `src/main/java/com/thinhreal/applestore/model/entity/UserEntity.java` |
| Refresh Token | `src/main/java/com/thinhreal/applestore/model/entity/RefreshTokenEntity.java` |
| Category | `src/main/java/com/thinhreal/applestore/model/entity/CategoryEntity.java` |
| Product | `src/main/java/com/thinhreal/applestore/model/entity/ProductEntity.java` |
| Order | `src/main/java/com/thinhreal/applestore/model/entity/OrderEntity.java` |
| Order Item | `src/main/java/com/thinhreal/applestore/model/entity/OrderItemEntity.java` |
