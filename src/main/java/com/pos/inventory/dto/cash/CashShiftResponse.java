package com.pos.inventory.dto.cash;

import com.pos.inventory.entity.CashShift;
import com.pos.inventory.entity.ShiftStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashShiftResponse {

    private Long id;
    private String shiftNumber;
    private Long cashierId;
    private String cashierName;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal expectedBalance;
    private BigDecimal difference;
    private ShiftStatus status;
    private String notes;

    public static CashShiftResponse fromEntity(CashShift shift) {
        return CashShiftResponse.builder()
                .id(shift.getId())
                .shiftNumber(shift.getShiftNumber())
                .cashierId(shift.getCashier() != null ? shift.getCashier().getId() : null)
                .cashierName(shift.getCashier() != null ? shift.getCashier().getFullName() : null)
                .openedAt(shift.getOpenedAt())
                .closedAt(shift.getClosedAt())
                .openingBalance(shift.getOpeningBalance())
                .closingBalance(shift.getClosingBalance())
                .expectedBalance(shift.getExpectedBalance())
                .difference(shift.getDifference())
                .status(shift.getStatus())
                .notes(shift.getNotes())
                .build();
    }
}
