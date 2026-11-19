-- V3 Seed Initial Retail Products
INSERT INTO products (id, name, sku, barcode, description, price, stock_quantity, low_stock_threshold, category_id, active) VALUES
(1, 'Logitech M185 Wireless Mouse', 'ELEC-LOGI-M185', '097855078513', '2.4GHz Wireless Optical Mouse with Nano USB Receiver', 799.00, 45, 5, 1, TRUE),
(2, 'Samsung 25W Type-C Fast Charger', 'ELEC-SAMS-25W', '8806090141699', 'Super Fast Charging Wall Adapter, Power Delivery 3.0', 1299.00, 8, 5, 1, TRUE),
(3, 'Nescafe Classic Instant Coffee Jar 200g', 'GROC-NESC-200G', '8901058852325', '100% Pure Natural Coffee made with selected Arabica & Robusta beans', 599.00, 60, 10, 2, TRUE),
(4, 'Classmate Long Notebook 400 Pages', 'STAT-CLAS-400P', '8903183000547', 'Single Line Spiral Ruled Long Exercise Book, High-Grade Paper', 160.00, 4, 10, 3, TRUE),
(5, 'boAt Rockerz 255 Pro+ Earphones', 'ELEC-BOAT-255P', '8904358823106', 'Bluetooth Wireless in-Ear Neckband with ASAP Fast Charge', 1499.00, 25, 5, 1, TRUE),
(6, 'SanDisk Ultra 64GB USB 3.0 Pen Drive', 'ELEC-SAND-USB64', '619659102456', 'SanDisk Cruzer Blade/Ultra 64GB High Speed USB Flash Drive', 549.00, 35, 5, 1, TRUE),
(7, 'Tata Tea Gold Premium 500g', 'GROC-TATA-500G', '8901052002139', 'Exquisite blend of fine CTC teas with gently rolled long leaves', 340.00, 40, 8, 2, TRUE),
(8, 'Cadbury Dairy Milk Silk Chocolate 150g', 'GROC-CADB-SILK', '7622201402324', 'Delicious creamy milk chocolate bar for gifting and celebration', 175.00, 50, 10, 2, TRUE),
(9, 'Amul Pasteurised Butter 500g', 'GROC-AMUL-500G', '8901262010054', 'Delicious pure salted table butter made from fresh cream', 275.00, 30, 8, 2, TRUE),
(10, 'Parker Jotter Stainless Steel Ball Pen', 'STAT-PARK-JOTT', '8901198000013', 'Iconic Stainless Steel Ballpoint Pen with Chrome Trim', 320.00, 15, 5, 3, TRUE),
(11, 'Casio FX-991CW Scientific Calculator', 'STAT-CASI-991CW', '4549526615598', 'ClassWiz Non-Programmable Scientific Calculator with 540 Functions', 1295.00, 12, 4, 3, TRUE),
(12, 'Post-it 3x3 Self-Adhesive Notes Pack', 'STAT-POST-3X3N', '051131650021', 'Yellow Repositionable Sticky Notes Pad, 100 Sheets per Pad', 145.00, 22, 5, 3, TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);
