# NextStore System Architecture & Design Specification

## System Overview
NextStore is an enterprise-grade Point of Sale (POS) and Inventory Management System designed with Spring Boot 3.3.5 and MySQL 8.0.

```
+------------------------------------------------------------------------+
|                          NextStore Frontend                            |
|  (Responsive Light Mode UI, Dynamic UPI QR, Camera Barcode Scanner,    |
|   Role-Based Workspaces, WhatsApp/SMS Receipts, AI Demand Forecaster)  |
+------------------------------------------------------------------------+
                                    |
                             HTTP / REST APIs
                                    v
+------------------------------------------------------------------------+
|                      Spring Boot 3.3.5 Backend                         |
|   Security Filter Chain -> Rate Limiting (Bucket4j) -> JWT Auth Provider|
|   Controllers (Sale, Product, Category, User, Report, Cash Drawer)     |
|   Services (Transactional Atomic Checkout, OpenPDF, Apache POI Export) |
|   Repositories (Spring Data JPA) -> HikariCP Connection Pool           |
+------------------------------------------------------------------------+
                                    |
                                JDBC / SQL
                                    v
+------------------------------------------------------------------------+
|                       MySQL 8.0 Database                               |
|   pos_inventory_db (Tables: sales, sale_items, products, categories,   |
|   users, roles, cash_shifts, cash_entries, stock_movements)            |
+------------------------------------------------------------------------+
```
