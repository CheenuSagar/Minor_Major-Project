# NextStore Multi-Role Workspaces

Dynamic role switching without re-login for multi-purpose terminals.

### 1. Customer Self-Service View
Focus on product catalog browsing, cart building, and QR checkout.

### 2. Cashier POS Register View
Accelerated rapid checkout with barcode scanner, tender calculator, and printer trigger.

### 3. Store Manager Control View
Product catalog management, price updates, stock replenishment, and category filters.

### 4. Store Owner Executive View
Financial KPIs, gross margins, AI reorder recommendations, and peak traffic hours.

## Permission Gating
Client-side visibility filters dynamically toggle buttons and cards according to active role.

## Active Role Indicator
Avatar badge shows user initials (e.g., 'MG' for Manager, 'CU' for Customer).

## Session Persistence
Saves selected workspace role in `sessionStorage` for persistence across refreshes.

## Contextual Action Bar
Displays quick access icons (Scanner, Coupon, Receipt, AI) based on workspace role.
