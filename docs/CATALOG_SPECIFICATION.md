# Product Catalog Specification

## Category Filters
- All Products (Default)
- Electronics
- Grocery
- Stationery

## Real-Time Search
Instant client-side substring matching on product name, SKU, and barcode with debouncing.

## SanDisk Cruzer Blade Asset
Embedded high-res visual for SanDisk Ultra 64GB USB 3.0 Pen Drive.

## Base64 Inlining
Embedded local image as Base64 data URI to eliminate extra network requests and auth bypass.

## Stock Threshold Badges
Displays exact units in stock with Amber warning when units <= 5.

## Metadata Captions
Displays SKU code (e.g. `ELEC-SAND-USB64`) and category name beneath product title.

## Quantity Spinners
Allows cashier/customer to specify quantity (1 to stock) directly on product card.

## Button Feedback
CSS active feedback ripple on 'Add to Bill' button for responsive touchscreens.

## Empty States
Helpful prompt when no products match the search filter query.

## Layout Modes
Responsive multi-column CSS grid dynamically adapting to terminal screen width.
