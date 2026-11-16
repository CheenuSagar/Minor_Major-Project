package com.pos.inventory.service;

import com.pos.inventory.dto.sale.SaleRequest;
import com.pos.inventory.dto.sale.SaleResponse;
import com.pos.inventory.entity.PaymentStatus;
import com.pos.inventory.entity.Sale;
import com.pos.inventory.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SaleService {
    SaleResponse createSale(SaleRequest request, User cashier);
    SaleResponse getSaleById(Long id);
    SaleResponse getSaleByInvoice(String invoiceNumber);
    Page<SaleResponse> getAllSales(LocalDateTime fromDate, LocalDateTime toDate, PaymentStatus status, Pageable pageable);
    Sale getSaleEntityById(Long id);
}
