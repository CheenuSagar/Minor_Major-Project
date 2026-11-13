package com.pos.inventory.dto.cash;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloseShiftRequest {

    @NotNull(message = "Closing balance is required")
    @DecimalMin(value = "0.00", message = "Closing balance cannot be negative")
    private BigDecimal closingBalance;

    private String notes;
}
