# NextStore Database Schema & Entity Dictionary

## Tables
1. `users` - Store operators, cashiers, managers, and registered customers
2. `roles` - Security role definitions (ROLE_STORE_MANAGER, ROLE_CASHIER, ROLE_CUSTOMER)
3. `user_roles` - Many-to-many junction between users and roles
4. `categories` - Product classifications (Electronics, Grocery, Stationery)
5. `products` - Retail inventory master with SKU, barcode, unit price, and stock levels
6. `sales` - Completed and pending invoice transactions with totals, tax, and discount
7. `sale_items` - Granular line items per sale with quantity, price, and GST breakdown
8. `payments` - Payment transaction records (CASH, UPI, CARD)
9. `cash_shifts` - Shift drawer opening/closing floats and audit reconciliations
10. `cash_entries` - Cash in/out manual adjustments during active shift
11. `stock_movements` - Comprehensive inventory replenishment and deduction audit log
12. `token_blacklist` - JWT tokens invalidated upon user logout
