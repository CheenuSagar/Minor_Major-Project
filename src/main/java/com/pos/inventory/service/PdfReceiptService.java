package com.pos.inventory.service;

import com.pos.inventory.entity.Sale;

public interface PdfReceiptService {
    byte[] generateReceiptPdf(Sale sale);
}
