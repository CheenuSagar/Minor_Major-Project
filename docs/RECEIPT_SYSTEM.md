# Digital Receipts & Multi-Channel Sharing

## WhatsApp Dispatch
Opens `https://wa.me/?text={ENCODED_RECEIPT}` with formatted itemized invoice.

## SMS Dispatch
Uses `sms:?body={ENCODED_RECEIPT}` web intent for mobile POS devices.

## Thermal Receipt Formatting
Standard 80mm thermal receipt layout with monospace font alignment.

## Thermal Printing
`window.print()` using CSS `@media print` rules hiding UI chrome and headers.

## PDF Export
Integrated with OpenPDF backend service for downloadable tax invoice PDF.

## Phone Number Capture
Optional phone number prompt during checkout for digital paperless delivery.

## Verification QR
Embedded QR code printed on receipt allows customer to view digital copy online.
