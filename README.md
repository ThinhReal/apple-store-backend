# 🚀 The Challenge Series
## Challenge 16: The Complex Input (Mastering Request Mapping)
- Objective: Handle various and complex data types sent from the client.

- Task: Develop a search API GET /api/v1/products/search that accepts:

    - @RequestParam for the product name.

    - @RequestHeader to retrieve a "store-token".

    - A List<String> tags for filtering.

    - An Enum Category (LAPTOP, SMARTPHONE, ACCESSORIES).

- Outcome: Successful mapping of all parameters from a Postman query like: ?name=macbook&tags=apple,m3&category=LAPTOP.

## Challenge 17: The Response Specialist (ResponseEntity & Headers)
- Objective: Master HTTP status codes and custom headers to provide meaningful responses.

- Task: Implement POST /api/v1/products.

  - On success: Return 201 Created status.

  - Include the created object in the ResponseEntity body.

  - Add a custom header named X-Product-Id.

- Outcome: Postman shows a green 201 Created status and the X-Product-Id key in the Headers tab.

## Challenge 18: The Fortress (Spring Validation & BindingResult)
- Objective: Prevent "dirty" or invalid data from entering the system.

- Task: * Apply constraints in ProductRequestDTO: @NotBlank (name), @Min(100) (price), and @Size(max=10) (tags).

    - In the Controller, use BindingResult to manually intercept errors and log them before they reach the handler.

    - Outcome: Sending a product with a price of -50 triggers a 400 Bad Request with a detailed error map.

## Challenge 19: The AOP Exception Shield (Custom vs. System)
- Objective: Use Aspect-Oriented Programming (AOP) to mask sensitive system errors while exposing user-friendly custom errors.

- Task: * Create a ProductNotFoundException (Custom) and a generic SystemBusyException.

    - Implement a @RestControllerAdvice.

    - Logic: If it's a Custom Exception, return the specific message. If it's a System Exception (like SQL errors), return a generic: "System is busy, please try again later."

- Outcome: API returns specific messages for "Product Not Found" but hides raw Java stack traces for internal crashes.

## Challenge 20: The Ultimate Integration (End-to-End Workflow)
- Objective: Combine all previous skills into a single, flawless API endpoint.

- Task: Create PUT /api/v1/products/{id}.

    - Must use @Valid for the request body.

    - Check ID existence using @PathVariable.

    - If the ID is missing, throw a Custom Exception.

    - Handle all mapping via ModelMapper in the Service layer.

- Outcome: A professional, "skinny" Controller with no try-catch blocks, managing full CRUD logic with total data safety.
