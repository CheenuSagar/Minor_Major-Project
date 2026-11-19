# POS Hardware Integration Guide

## HTML5 Camera Barcode Scanner
Accesses webcam or rear camera via `navigator.mediaDevices.getUserMedia`.

## Audio Feedback
Web Audio API synthesizes a 1800Hz sine wave tone for 80ms on valid barcode detection.

## Aiming Reticle
Animated red laser scanning beam provides visual targeting guide for cashier.

## Auto-Add to Bill
Scanned SKU immediately increments quantity in cart without manual click.
