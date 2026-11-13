package com.pos.inventory.dto.payment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockGatewayPaymentResponse {

    private boolean success;
    private String transactionId;
    private String authorizationCode;
    private String responseCode;
    private String message;
    private LocalDateTime timestamp;
}
