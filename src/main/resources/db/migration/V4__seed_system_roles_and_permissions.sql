-- V4 Seed System Roles
INSERT INTO roles (id, name, description) VALUES
(1, 'ROLE_STORE_MANAGER', 'Full administrative authority over inventory, staff, and pricing'),
(2, 'ROLE_CASHIER', 'POS register billing, barcode scanning, and shift drawer balancing'),
(3, 'ROLE_OWNER', 'Executive oversight, AI demand forecasting, and profit analytics'),
(4, 'ROLE_CUSTOMER', 'Catalogue browsing, cart management, and digital UPI checkout')
ON DUPLICATE KEY UPDATE name=VALUES(name);
