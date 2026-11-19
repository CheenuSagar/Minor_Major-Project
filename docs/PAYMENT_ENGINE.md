# NextStore Digital Payment & UPI QR Engine

Dynamic UPI QR generation for contactless payments at retail billing counters.

## UPI Intent Protocol
URI Format: `upi://pay?pa=store@upi&pn=NextStore%20Retail&am={TOTAL}&cu=INR&tn=Invoice_{INV}`

## Modal Presentation
Displays crisp QR code modal on screen with invoice total and store VPA.

## Supported UPI Apps
Compatible with Google Pay, PhonePe, Paytm, BHIM, Amazon Pay, and all UPI 2.0 apps.

## Tender Modes
- CASH (Cash Tendered, Change Due)
- UPI QR (Instant Dynamic QR)
- CARD (Debit/Credit POS Swipe)

## Cash Tender Calculator
Cashier inputs amount received; system instantly computes exact change to return.
