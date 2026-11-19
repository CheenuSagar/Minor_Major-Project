# Billing Register & Cart State Engine

Reactive cart state maintaining line items, unit prices, and quantities.

## Line Item Removal
Instant trash icon removes item and triggers cart total recalculation.

## GST Calculation
Standard 18% GST split into CGST (9%) and SGST (9%) line breakdowns.

## Sticky Summary
Fixed subtotal, tax amount, discount deduction, and net payable grand total.

## Clear Bill
Quick 'Clear Register' button resets current active cart for the next customer.

## Cart Counter Badge
Badge in the top right shows total number of items currently in the active bill.

## Validation
Checkout button disabled with tooltip when cart is empty.

## Keyboard Navigation
Keyboard navigation keys allow hands-free register operation.

## Customer Notes
Optional customer name and reference notes attached to sale transaction.

## State Persistence
Cart contents stored in browser storage to guard against accidental browser close.
