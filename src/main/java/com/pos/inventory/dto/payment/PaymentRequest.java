package com.pos.inventory.dto.payment;

import com.pos.inventory.entity.PaymentMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "Sale ID is required")
    private Long saleId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment mode is required (CASH or DIGITAL)")
    private PaymentMode paymentMode;

    // Optional fields for Mock Digital Gateway sandbox simulation
    private String cardNumber;
    private String cardExpiry;
    private String upiId;
    private String notes;
}
