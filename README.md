# 🍎 Apple Store - iStore REST API

Welcome to the backend project for the **Apple Store** system. This is a professional e-commerce application built using Spring Boot, MySQL Cloud (Aiven), and Swagger.

## 🛠️ System Requirements
- **Java 17+**

- **Maven 3.x**

- **Aiven.io** account (or local MySQL)

## 🔑 Setup Instructions

For security reasons, sensitive information has been hidden. To run the project, you need to set up the following **Environment Variables** in your IDE:

| Variable | Description | Example |

| :--- | :--- | :--- |

| `DB_URL` | Database connection path | `jdbc:mysql://host:port/defaultdb?ssl-mode=REQUIRED` |

| `DB_USERNAME` | DB Username | `avnadmin` |

| `DB_PASSWORD` | DB Password | `your_secret_password` |

### Launch Steps:
1. Clone the project: `git clone <your-repo-url>`
2. Open the project using IntelliJ IDEA.

3. Set Environment Variables in **Run/Debug Configurations**.

4. Run the application.

5. Access Swagger UI at: `http://localhost:8080/swagger-ui/index.html`

## 👨‍💻 Author
- **Thịnh Real** - Backend Developer
## Challenge 1: The Blueprint (Setup, Entities & Swagger)
Before writing business logic, a system needs a foundation and clear documentation so the Frontend team knows what to expect.

- Objective: Design the database schema and expose the API documentation.

- Tasks:

  - Initialize the Spring Boot project with Web, JPA, SQL (MySQL/PostgreSQL), and SpringDoc (Swagger) dependencies.

  - Create the core Entities: User, Product, Order, and OrderItem.
    ![ERD](https://res.cloudinary.com/drw6fqjgr/image/upload/v1774769901/Screenshot_2026-03-29_at_14.38.04_nalcyu.png)

  - Configure Swagger to display at http://localhost:8080/swagger-ui.html.

- Outcome: You have a running application that connects to an SQL database, auto-generates the tables, and displays a beautiful, interactive API documentation page.

## Challenge 2: The Catalog (Advanced CRUD, Search & Pagination)
An Apple Store has hundreds of products. Sending them all at once will crash the app. We need pagination and dynamic searching.

- Objective: Build the Product management APIs for both Customers and Admins.

- Tasks:

  - Implement standard CRUD for Products (Admin only conceptually for now).

  - Implement GET /api/v1/products using Spring Data JPA's Pageable.

  - Add custom query methods in the Repository to search by name (e.g., "iPhone") or filter by category (e.g., "MAC").

- Outcome: You can send a request to /products?page=0&size=5&search=Pro and receive exactly 5 items along with total page metadata.

## Challenge 3: The Apple ID (JWT Authentication & Authorization)
This is the most critical part. You cannot allow anyone to create an order or delete a product without proving who they are and what their role is.

- Objective: Secure the API using JSON Web Tokens (JWT) and Role-Based Access Control (RBAC).

- Tasks:

  - Integrate Spring Security.

  - Create POST /auth/register and POST /auth/login.

  - Write a utility class to generate and validate JWTs.

  - Create a JWT Filter to intercept requests and check for the Authorization: Bearer <token> header.

  - Restrict POST /products to ADMIN roles and POST /orders to CUSTOMER roles.

- Outcome: Trying to delete an iPhone without an Admin token returns a 403 Forbidden. Logging in successfully returns a valid JWT.

## Challenge 4: The Checkout (Relational Mapping & Transactions)
E-commerce is all about the checkout. This requires handling multiple database tables simultaneously while ensuring data integrity.

- Objective: Build the ordering system using complex Entity relationships mapping and @Transactional.

- Tasks:

  - Map the One-to-Many and Many-to-Many relationships between User, Order, and OrderItem.

  - Create POST /api/v1/orders. The payload should include a list of product IDs and quantities.

  - Implement logic to calculate the total price and deduct inventory.

  - Use @Transactional to ensure that if saving the order fails, the inventory deduction rolls back.

- Outcome: A user can submit an order for an iPhone and AirPods. The system creates the order, links it to the user, and updates the stock, all in one secure transaction.

## Challenge 5: The QA Engineer (Unit Testing)
Code isn't finished until it's tested. Professional environments require Unit Tests to ensure future changes don't break existing features.

- Objective: Write automated tests for your application to guarantee stability.

- Tasks:

  - Use JUnit 5 and Mockito to test the ProductService.

  - Mock the ProductRepository so the test doesn't actually hit the SQL database.

  - Write tests for "Product Found" and "Product Not Found" (Custom Exception) scenarios.

  - (Bonus) Use MockMvc to test the Controller endpoints.

- Outcome: You can run mvn test and see a beautiful green report proving your logic works flawlessly without starting the actual server.