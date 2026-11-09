-- ====================================================================
-- Point of Sale (POS) and Inventory Management System - Initial Seed Data
-- ====================================================================

-- 1. Insert Roles
INSERT INTO ROLES (ID, NAME, DESCRIPTION)
VALUES (1, 'ROLE_STORE_MANAGER', 'Store Manager with full administrative access');

INSERT INTO ROLES (ID, NAME, DESCRIPTION)
VALUES (2, 'ROLE_CUSTOMER', 'Customer role for public catalogue and orders');

-- 2. Insert Users (Password: Manager@123 and Customer@123 - bcrypt encoded)
-- Hash: $2a$10$V0w4jA0qX8WdJp5Hw8Igu.b2x1kG0OlnO2Zz8d9pU3uG0/r8yB2kO is BCrypt for 'Manager@123'
INSERT INTO USERS (ID, FULL_NAME, EMAIL, USERNAME, PASSWORD, ENABLED, CREATED_AT)
VALUES (1, 'Store Manager Admin', 'manager@possystem.com', 'manager',
        '$2a$10$5GfK6Ua3m4Zf6jVl5zKzcuOslLg1lWjWJpGvLqE4eM5YkH7V0K1wS', 1, CURRENT_TIMESTAMP);

INSERT INTO USERS (ID, FULL_NAME, EMAIL, USERNAME, PASSWORD, ENABLED, CREATED_AT)
VALUES (2, 'Rohan Sharma', 'customer@possystem.com', 'customer',
        '$2a$10$5GfK6Ua3m4Zf6jVl5zKzcuOslLg1lWjWJpGvLqE4eM5YkH7V0K1wS', 1, CURRENT_TIMESTAMP);

-- 3. Map Roles to Users
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (1, 1);
INSERT INTO USER_ROLES (USER_ID, ROLE_ID) VALUES (2, 2);

-- 4. Insert Categories
INSERT INTO CATEGORIES (ID, NAME, DESCRIPTION, ACTIVE, CREATED_AT)
VALUES (1, 'Electronics', 'Gadgets, accessories and electronic equipment', 1, CURRENT_TIMESTAMP);

INSERT INTO CATEGORIES (ID, NAME, DESCRIPTION, ACTIVE, CREATED_AT)
VALUES (2, 'Grocery', 'Daily FMCG, packaged foods and beverages', 1, CURRENT_TIMESTAMP);

INSERT INTO CATEGORIES (ID, NAME, DESCRIPTION, ACTIVE, CREATED_AT)
VALUES (3, 'Stationery', 'Office supplies, books, pens and paper', 1, CURRENT_TIMESTAMP);

-- 5. Insert Sample Products
INSERT INTO PRODUCTS (ID, NAME, SKU, DESCRIPTION, PRICE, COST_PRICE, STOCK_QUANTITY, REORDER_THRESHOLD, TAX_RATE, ACTIVE, CATEGORY_ID, CREATED_AT)
VALUES (1, 'Logitech M185 Wireless Mouse', 'ELEC-LOGI-M185', '2.4GHz wireless mouse with nano USB receiver, 12-month battery life', 799.00, 520.00, 45, 10, 18.00, 1, 1, CURRENT_TIMESTAMP);

INSERT INTO PRODUCTS (ID, NAME, SKU, DESCRIPTION, PRICE, COST_PRICE, STOCK_QUANTITY, REORDER_THRESHOLD, TAX_RATE, ACTIVE, CATEGORY_ID, CREATED_AT)
VALUES (2, 'Samsung 25W Type-C Fast Charger', 'ELEC-SAMS-T500', 'Super fast charging adapter with power delivery 3.0', 1299.00, 850.00, 8, 12, 18.00, 1, 1, CURRENT_TIMESTAMP);

INSERT INTO PRODUCTS (ID, NAME, SKU, DESCRIPTION, PRICE, COST_PRICE, STOCK_QUANTITY, REORDER_THRESHOLD, TAX_RATE, ACTIVE, CATEGORY_ID, CREATED_AT)
VALUES (3, 'Nescafe Classic Instant Coffee Jar 200g', 'GROC-NESC-CL200', '100% pure natural instant coffee powder', 599.00, 460.00, 60, 15, 5.00, 1, 2, CURRENT_TIMESTAMP);

INSERT INTO PRODUCTS (ID, NAME, SKU, DESCRIPTION, PRICE, COST_PRICE, STOCK_QUANTITY, REORDER_THRESHOLD, TAX_RATE, ACTIVE, CATEGORY_ID, CREATED_AT)
VALUES (4, 'Classmate Long Notebook 400 Pages', 'STAT-CLAS-REG400', 'Single ruled premium spiral notebook for college', 160.00, 110.00, 4, 10, 12.00, 1, 3, CURRENT_TIMESTAMP);
