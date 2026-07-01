# OpenAPI contract-first workflow

The API contract lives in `openapi/openapi.yaml`.

## Generate Java code from the spec

```bash
./mvnw generate-sources
```

Generated files appear under:

```
target/generated-sources/openapi/src/main/java/
  com/thinhreal/applestore/api/ProductsApi.java      # API interface
  com/thinhreal/applestore/api/model/ProductDTO.java # DTOs
```

## Implement the generated interface

Create a `@RestController` that implements the generated `*Api` interface
(for example `ProductApiController implements ProductsApi`).

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
3. Implement or update the controller that implements the generated interface
4. Run the app and test in Swagger UI
