package com.pos.inventory.dto.sale;

import com.pos.inventory.entity.PaymentStatus;
import com.pos.inventory.entity.Sale;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponse {

    private Long id;
    private String invoiceNumber;
    private LocalDateTime saleDate;
    private Long cashierId;
    private String cashierName;
    private Long customerId;
    private String customerName;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String notes;
    private List<SaleItemResponse> items;
    private LocalDateTime createdAt;

    public static SaleResponse fromEntity(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId())
                .invoiceNumber(sale.getInvoiceNumber())
                .saleDate(sale.getSaleDate())
                .cashierId(sale.getCashier() != null ? sale.getCashier().getId() : null)
                .cashierName(sale.getCashier() != null ? sale.getCashier().getFullName() : null)
                .customerId(sale.getCustomer() != null ? sale.getCustomer().getId() : null)
                .customerName(sale.getCustomer() != null ? sale.getCustomer().getFullName() : "Walk-in Customer")
                .subtotal(sale.getSubtotal())
                .discountAmount(sale.getDiscountAmount())
                .taxAmount(sale.getTaxAmount())
                .totalAmount(sale.getTotalAmount())
                .paymentStatus(sale.getPaymentStatus())
                .notes(sale.getNotes())
                .items(sale.getItems() != null ? sale.getItems().stream().map(SaleItemResponse::fromEntity).collect(Collectors.toList()) : List.of())
                .createdAt(sale.getCreatedAt())
                .build();
    }
}
