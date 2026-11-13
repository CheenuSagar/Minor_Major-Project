package com.pos.inventory.dto.payment;

import com.pos.inventory.entity.Payment;
import com.pos.inventory.entity.PaymentMode;
import com.pos.inventory.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private Long saleId;
    private String invoiceNumber;
    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private BigDecimal saleTotal;
    private BigDecimal changeDue;
    private PaymentMode paymentMode;
    private PaymentStatus status;
    private String transactionReference;
    private String gatewayResponse;
    private String notes;
    private String recordedByName;

    public static PaymentResponse fromEntity(Payment payment) {
        BigDecimal total = payment.getSale() != null ? payment.getSale().getTotalAmount() : BigDecimal.ZERO;
        BigDecimal change = payment.getAmount().compareTo(total) > 0 ? payment.getAmount().subtract(total) : BigDecimal.ZERO;

        return PaymentResponse.builder()
                .id(payment.getId())
                .saleId(payment.getSale() != null ? payment.getSale().getId() : null)
                .invoiceNumber(payment.getSale() != null ? payment.getSale().getInvoiceNumber() : null)
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .saleTotal(total)
                .changeDue(change)
                .paymentMode(payment.getPaymentMode())
                .status(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .gatewayResponse(payment.getGatewayResponse())
                .notes(payment.getNotes())
                .recordedByName(payment.getRecordedBy() != null ? payment.getRecordedBy().getFullName() : null)
                .build();
    }
}
