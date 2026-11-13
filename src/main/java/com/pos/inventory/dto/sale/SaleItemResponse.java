package com.pos.inventory.dto.sale;

import com.pos.inventory.entity.SaleItem;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItemResponse {

    private Long id;
    private Long productId;
    private String productSku;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal total;

    public static SaleItemResponse fromEntity(SaleItem item) {
        return SaleItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productSku(item.getProductSku())
                .productName(item.getProductName())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .taxRate(item.getTaxRate())
                .taxAmount(item.getTaxAmount())
                .total(item.getTotal())
                .build();
    }
}
