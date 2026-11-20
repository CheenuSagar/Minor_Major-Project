# Digital Receipts & Multi-Channel Sharing

## WhatsApp Dispatch
Opens `https://wa.me/?text={ENCODED_RECEIPT}` with formatted itemized invoice.

## SMS Dispatch
Uses `sms:?body={ENCODED_RECEIPT}` web intent for mobile POS devices.

## Thermal Receipt Formatting
Standard 80mm thermal receipt layout with monospace font alignment.

## Thermal Printing
`window.print()` using CSS `@media print` rules hiding UI chrome and headers.
