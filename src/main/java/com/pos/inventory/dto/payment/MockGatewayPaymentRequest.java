package com.pos.inventory.dto.payment;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockGatewayPaymentRequest {

    private String invoiceNumber;
    private BigDecimal amount;
    private String cardNumber;
    private String cardExpiry;
    private String upiId;
    private String customerEmail;
}
