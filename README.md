# Point of Sale (POS) and Inventory Management System

A production-grade, secure RESTful backend system built according to **IEEE 830 SRS guidelines** for a Master of Computer Applications (MCA) capstone project.

---

## 🛠 Tech Stack

| Component | Technology |
|---|---|
| **Language & Framework** | Java 17 (OpenJDK 17), Spring Boot 3.3.5 |
| **Build & Dependency Tool** | Apache Maven 3.9+ |
| **Persistence & ORM** | Spring Data JPA, Hibernate, Oracle Dialect |
| **Database** | Oracle Database 21c XE (port 1521, `XEPDB1` / `XE`), H2 In-Memory (Test/Fallback) |
| **Security & Auth** | Spring Security 6, JWT (`jjwt` 0.12.6, HMAC-SHA256), BCrypt hashing |
| **API Documentation** | Springdoc OpenAPI 3 (Swagger UI) |
| **Reporting & Export** | OpenPDF (PDF receipts & reports), Apache POI 5.3 (Excel XLSX export) |
| **Rate Limiting** | Bucket4j (Token Bucket algorithm, 100 requests/minute per client IP) |
| **Observability** | Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`) |
| **Testing** | JUnit 5, Mockito |

---

## 📐 System Architecture

Layered enterprise architecture following SOLID principles and Clean Architecture:
```
[Client / Frontend / Postman / Swagger UI]
                  │ (HTTP/REST + JWT Bearer)
                  ▼
         [RateLimitingFilter]  <-- 100 req/min per IP
                  │
        [JwtAuthenticationFilter] <-- HS256 Token Validation & Blacklist Check
                  │
        [REST Controllers]  <-- Controller Layer (@Valid, DTOs)
                  │
         [Service Layer]    <-- Business Logic, Transactions (@Transactional)
                  │
        [Repository Layer]  <-- Spring Data JPA Repositories (Parameterized queries)
                  │
       [Oracle 21c Database] <-- Tables, Indexes, Sequences, Foreign Keys
```

### Roles and Permissions
- `ROLE_STORE_MANAGER`: Complete administrative control (Product CRUD, stock in/adjustment, viewing low stock alerts, opening/closing cash drawers, sales reports, user management, PDF/Excel exports).
- `ROLE_CUSTOMER`: Access to public catalogue, checkout/order placement, viewing their own receipts.
- **Guest / Public**: Read-only access to `/api/public/products` (catalogue search & category filtering) and Swagger/Actuator.

---

## 🚀 Step-by-Step Functional Modules (IEEE 830 SRS)

### 1. Authentication & Access Control (FR-1.x)
- **POST `/api/auth/register`**: Validates user inputs (name, email, username, strong password min 8 chars with letters, numbers, and symbols) and creates account with BCrypt password hash.
- **POST `/api/auth/login`**: Authenticates credentials and returns a signed HS256 JWT valid for 120 minutes.
- **POST `/api/auth/logout`**: Extracts the bearer token and adds it to the persistent `TOKEN_BLACKLIST` table to prevent reuse.
- **PATCH `/api/users/{id}/status`**: Store Manager endpoint to activate or deactivate user accounts.
- **PUT `/api/users/{id}/roles`**: Store Manager endpoint to grant or revoke system roles.

### 2. Product Catalogue & Inventory Management (FR-2.x)
- **Product CRUD**: Products with unique SKU/barcode, category, cost price, retail price (INR), current stock, reorder threshold, and soft deactivation flag.
- **GET `/api/public/products`**: Public search by keyword and category with pagination and sorting.
- **POST `/api/inventory/stock-in`**: Adds incoming stock from supplier delivery with reason and purchase order reference.
- **POST `/api/inventory/adjust`**: Adjusts stock for damage/shrinkage with mandatory explanation.
- **GET `/api/inventory/low-stock`**: Alerts when product stock quantity falls below or equal to its reorder threshold.
- **GET `/api/inventory/movements/{productId}`**: Full audit log of all stock movements (Stock-In, Sale Deductions, Adjustments).

### 3. Sales Transactions & PDF Receipts (FR-3.x)
- **POST `/api/sales`**: POS billing checkout. Checks stock sufficiency for all line items. Calculates item subtotals, GST tax, discounts, and total. In **one single `@Transactional` method**, it:
  1. Validates inventory levels
  2. Automatically decrements stock quantities
  3. Records audit entries in `STOCK_MOVEMENTS`
  4. Saves `SALE` and `SALE_ITEMS`
  5. Rolls back completely if any product stock is insufficient.
- **GET `/api/sales/{id}/receipt`**: Generates and downloads a clean, printable **PDF receipt** using OpenPDF containing company header, invoice number, line items, tax breakdown, and total.

### 4. Payments & Cash Drawer Management (FR-4.x)
- **POST `/api/payments`**:
  - `CASH`: Verifies tendered cash >= bill amount, calculates change due, updates sale to `PAID`, and logs entry into the active cashier drawer.
  - `DIGITAL`: Calls a mock sandbox payment gateway service that simulates approvals (or simulated decline if card ends with `0000`).
- **Cash Drawer**:
  - **POST `/api/cash-drawer/shift/open`**: Open shift with starting cash float.
  - **POST `/api/cash-drawer/entry`**: Record paid-in or paid-out petty cash.
  - **POST `/api/cash-drawer/shift/{id}/close`**: Close shift, calculate expected balance vs actual physical cash counted, and compute variance.

### 5. Analytics & Reports (FR-5.x)
- **GET `/api/reports/dashboard`**: Daily, weekly, monthly sales totals, completed order counts, low stock product count, and top 5 best-selling products.
- **GET `/api/reports/sales?format=pdf|xlsx`**: Sales report exportable to Excel (.xlsx via Apache POI) or PDF (via OpenPDF).
- **GET `/api/reports/inventory?format=pdf|xlsx`**: Inventory valuation report at cost and retail.
- **GET `/api/reports/cash?format=pdf|xlsx`**: Cash drawer reconciliation report.

---

## ⚙️ Configuration & Environment Variables

All database and security credentials are externalized via `application.yml` and can be set through environment variables:

| Variable | Default Value | Description |
|---|---|---|
| `PORT` | `8080` | Server HTTP port |
| `DB_URL` | `jdbc:oracle:thin:@//localhost:1521/XEPDB1` | Oracle 21c XE connection URL |
| `DB_USERNAME` | `pos_user` | Oracle Database username |
| `DB_PASSWORD` | `pos_pass` | Oracle Database password |
| `DB_DRIVER` | `oracle.jdbc.OracleDriver` | JDBC driver class |
| `JPA_DIALECT` | `org.hibernate.dialect.OracleDialect` | Hibernate Oracle dialect |
| `JWT_SECRET` | `404E6352...` | 256-bit secret key for HMAC-SHA256 |
| `JWT_EXPIRATION_MS` | `7200000` (120 mins) | Token validity |
| `CORS_ALLOWED_ORIGINS`| `http://localhost:4200` | Allowed CORS origin (Angular/React frontend) |

---

## 🏃 Quick Start Guide

### 1. Build and Run Unit Tests
```bash
./mvnw clean test
```

### 2. Run the Application
```bash
# Using default Oracle or setting your credentials:
export DB_URL="jdbc:oracle:thin:@//localhost:1521/XEPDB1"
export DB_USERNAME="pos_user"
export DB_PASSWORD="your_password"
./mvnw spring-boot:run
```

*Note: If Oracle is not currently running locally, run with the in-memory H2 profile for instant testing:*
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

### 3. Access Swagger UI & Documentation
Open in browser:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Actuator Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 👥 Default Seed Accounts

The application automatically seeds the following credentials via `DataInitializer` and `data.sql`:

| Role | Username | Password | Email |
|---|---|---|---|
| **Store Manager** | `manager` | `Manager@123` | `manager@possystem.com` |
| **Customer** | `customer` | `Customer@123` | `customer@possystem.com` |

---

## 🧪 Postman Collection Setup
1. Open **Postman** -> Click **Import** -> Select `pos_inventory_postman_collection.json`.
2. Run `Login as Store Manager` - the test script automatically captures the JWT token and saves it in the collection variable `{{managerToken}}`.
3. Execute requests across the 5 modules in sequence.
