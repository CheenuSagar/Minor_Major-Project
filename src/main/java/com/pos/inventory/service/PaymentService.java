package com.pos.inventory.service;

import com.pos.inventory.dto.payment.PaymentRequest;
import com.pos.inventory.dto.payment.PaymentResponse;
import com.pos.inventory.entity.User;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request, User recordedBy);
    List<PaymentResponse> getPaymentsForSale(Long saleId);
    PaymentResponse getPaymentById(Long id);
}
