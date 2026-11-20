# NextStore REST API Reference Guide

## Base URL: `http://localhost:8080/api`

### 1. Authentication (`/api/auth`)
- `POST /login` - Authenticate user & receive JWT token
- `POST /register` - Register new customer account
- `POST /logout` - Invalidate active JWT token

### 2. Products & Inventory (`/api/products`)
- `GET /` - List all products (paginated)
- `GET /{id}` - Get product details by ID
- `POST /` - Create new product (ROLE_STORE_MANAGER)
- `PUT /{id}` - Update product details (ROLE_STORE_MANAGER)
- `DELETE /{id}` - Deactivate product (ROLE_STORE_MANAGER)
- `GET /low-stock` - List low stock alerts

### 3. Sales & Billing (`/api/sales`)
- `POST /` - Process new checkout sale transaction
- `GET /{id}` - Retrieve sale invoice by ID
- `GET /` - List past sales transactions
- `GET /{id}/receipt` - Stream PDF receipt document

### 4. Reports (`/api/reports`)
- `GET /dashboard` - Dashboard analytical KPI summaries
- `GET /sales/export/pdf` - Export comprehensive sales PDF
- `GET /sales/export/excel` - Export sales Excel workbook
