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


## Expanded Product Catalog (24 Products)

### Electronics (8 items)
1. `ELEC-LOGI-M185`: Logitech M185 Wireless Mouse (₹799)
2. `ELEC-SAMS-T500`: Samsung 25W Type-C Fast Charger (₹1299)
3. `ELEC-BOAT-ROK255`: boAt Rockerz 255 Pro+ Earphones (₹1499)
4. `ELEC-SAND-USB64`: SanDisk Cruzer Blade 64GB Pen Drive (₹549)
5. `ELEC-SONY-CH520`: Sony WH-CH520 Wireless Bluetooth Headphones (₹4490)
6. `ELEC-APPL-20W`: Apple 20W USB-C Power Adapter (₹1900)
7. `ELEC-PORT-USBC`: Portronics Konnect Pro Type-C Fast Cable 1.2M (₹249)
8. `ELEC-HP-X1000`: HP X1000 Wired Optical Mouse (₹399)

### Grocery (8 items)
9. `GROC-NESC-CL200`: Nescafe Classic Instant Coffee Jar 200g (₹599)
10. `GROC-TATA-TEA500`: Tata Tea Gold Premium 500g (₹340)
11. `GROC-CADB-SILK150`: Cadbury Dairy Milk Silk Chocolate 150g (₹175)
12. `GROC-AMUL-BUTTER`: Amul Pasteurised Butter 500g (₹275)
13. `GROC-FORT-OIL1L`: Fortune Sunlite Refined Sunflower Oil 1L (₹145)
14. `GROC-KELL-CORN500`: Kellogg's Corn Flakes Original 500g Box (₹215)
15. `GROC-MAGG-MAS12`: Maggi 2-Minute Masala Noodles 12-Pack (₹168)
16. `GROC-HALD-BHUJ400`: Haldiram's Nagpur Bhujia Sev 400g (₹130)

### Stationery (8 items)
17. `STAT-CLAS-REG400`: Classmate Long Notebook 400 Pages (₹160)
18. `STAT-PARK-JOTTER`: Parker Jotter Stainless Steel Ball Pen (₹320)
19. `STAT-CASI-FX991`: Casio FX-991CW Scientific Calculator (₹1295)
20. `STAT-POST-IT300`: Post-it 3x3 Self-Adhesive Notes Pack (₹145)
21. `STAT-CAML-WB4`: Camlin Kokuyo Whiteboard Marker Pack (4 Colours) (₹140)
22. `STAT-KANG-STAP10`: Kangaro No. 10 Heavy Duty Stapler with Pins (₹95)
23. `STAT-FEVI-GLUE15`: Fevistik Super Glue Stick 15g (Pack of 3) (₹105)
24. `STAT-CELL-BFLW10`: Cello Butterflow Classic Blue Ball Pen (Pack of 10) (₹100)
