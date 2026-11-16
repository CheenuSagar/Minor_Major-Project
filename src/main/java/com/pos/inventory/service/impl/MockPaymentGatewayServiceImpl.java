package com.pos.inventory.service.impl;

import com.pos.inventory.dto.payment.MockGatewayPaymentRequest;
import com.pos.inventory.dto.payment.MockGatewayPaymentResponse;
import com.pos.inventory.service.MockPaymentGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class MockPaymentGatewayServiceImpl implements MockPaymentGatewayService {

    @Override
    public MockGatewayPaymentResponse charge(MockGatewayPaymentRequest request) {
        log.info("Processing digital sandbox transaction for invoice: {}, amount: ₹{}",
                request.getInvoiceNumber(), request.getAmount());

        // Simulated decline scenario for test card ending with 0000
        if (request.getCardNumber() != null && request.getCardNumber().endsWith("0000")) {
            log.warn("Mock Gateway: Card declined by issuer for card ending in 0000");
            return MockGatewayPaymentResponse.builder()
                    .success(false)
                    .transactionId("TXN-FAIL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .responseCode("DECLINED_51")
                    .message("Insufficient funds or card declined by issuing bank")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        // Simulated Success
        String txnId = "TXN-SANDBOX-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        String authCode = "AUTH" + (int)(Math.random() * 900000 + 100000);

        log.info("Mock Gateway: Transaction approved. Ref: {}, Auth: {}", txnId, authCode);
        return MockGatewayPaymentResponse.builder()
                .success(true)
                .transactionId(txnId)
                .authorizationCode(authCode)
                .responseCode("SUCCESS_00")
                .message("Payment authorized and captured successfully via Mock Gateway")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
