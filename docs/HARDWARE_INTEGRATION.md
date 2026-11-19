# POS Hardware Integration Guide

## HTML5 Camera Barcode Scanner
Accesses webcam or rear camera via `navigator.mediaDevices.getUserMedia`.

## Audio Feedback
Web Audio API synthesizes a 1800Hz sine wave tone for 80ms on valid barcode detection.

## Aiming Reticle
Animated red laser scanning beam provides visual targeting guide for cashier.

## Auto-Add to Bill
Scanned SKU immediately increments quantity in cart without manual click.

## Scan Debouncer
1200ms refractory cooldown prevents rapid multiple readings of the same item.

## Handheld Barcode Scanners
Supports USB and Bluetooth handheld barcode scanners emulating keyboard wedge input.

## Manual Barcode Entry
Fallback text box allows typing numeric barcode or SKU directly.
