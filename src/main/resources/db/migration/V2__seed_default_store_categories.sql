-- V2 Seed Categories
INSERT INTO categories (id, name, description, active) VALUES
(1, 'Electronics', 'Electronics, Peripherals and Storage Devices', TRUE),
(2, 'Grocery', 'Packaged Food, Beverages and Essentials', TRUE),
(3, 'Stationery', 'Office Stationery, Notebooks and Writing Supplies', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);
