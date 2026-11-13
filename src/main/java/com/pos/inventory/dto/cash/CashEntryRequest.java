package com.pos.inventory.dto.cash;

import com.pos.inventory.entity.CashEntryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashEntryRequest {

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    @NotNull(message = "Entry type is required (CASH_IN or CASH_OUT)")
    private CashEntryType entryType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Reason is required")
    private String reason;
}
