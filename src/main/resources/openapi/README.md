# OpenAPI contract-first workflow

The API contract lives in `openapi/openapi.yaml`.

## Generate Java code from the spec

```bash
./mvnw generate-sources
```

Generated files appear under:

```
target/generated-sources/openapi/src/main/java/
  com/thinhreal/applestore/api/ProductsApi.java           # API interface
  com/thinhreal/applestore/api/model/RequestProductDTO.java
  com/thinhreal/applestore/api/model/ResponseProductDTO.java
```

## Implement the generated interface

Create a `@RestController` that implements the generated `*Api` interface
(for example `ProductController implements ProductsApi`).

## Compile and run

```bash
./mvnw clean compile
./mvnw spring-boot:run
```

## Swagger UI

http://localhost:8080/swagger-ui.html

## Workflow

1. Edit `openapi/openapi.yaml`
2. Run `./mvnw generate-sources`
3. Implement or update the controller and service that use the generated DTOs
4. Run the app and test in Swagger UI

## Product API notes

- **Request:** `RequestProductDTO` — requires `category_id`, `name`, `price`, `stock_quantity`; optional `description`
- **Response:** `ResponseProductDTO` — includes embedded `category` object for frontend convenience
- JSON field names use snake_case (e.g. `stock_quantity`, `category_id`)
