package com.pos.inventory.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "New quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer newQuantity;

    @NotBlank(message = "Adjustment reason is required")
    private String reason;
}
