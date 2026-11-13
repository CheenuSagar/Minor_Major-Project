package com.pos.inventory.dto.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleRequest {

    private Long customerId;

    @NotEmpty(message = "Sale must contain at least one item")
    @Valid
    private List<SaleItemRequest> items;

    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    private String notes;
}
