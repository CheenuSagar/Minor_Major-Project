# Backend Architecture & Persistence Hardening

## Custom Repository Queries
Added JPQL date-range queries in `SaleRepository` for executive reporting.

## Inventory Queries
Custom JPQL queries in `ProductRepository` returning items below `low_stock_threshold`.

## Transaction Isolation
`@Transactional` on `SaleServiceImpl.createSale()` ensures atomicity across inventory deduction and invoice creation.

## Concurrency Control
Optimistic stock deduction handles concurrent cashier checkouts without overselling.
