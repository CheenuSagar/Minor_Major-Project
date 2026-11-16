package com.pos.inventory.service;

import com.pos.inventory.dto.payment.MockGatewayPaymentRequest;
import com.pos.inventory.dto.payment.MockGatewayPaymentResponse;

public interface MockPaymentGatewayService {
    MockGatewayPaymentResponse charge(MockGatewayPaymentRequest request);
}
