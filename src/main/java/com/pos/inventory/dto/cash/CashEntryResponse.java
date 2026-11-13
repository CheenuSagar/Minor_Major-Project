package com.pos.inventory.dto.cash;

import com.pos.inventory.entity.CashEntry;
import com.pos.inventory.entity.CashEntryType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashEntryResponse {

    private Long id;
    private Long shiftId;
    private CashEntryType entryType;
    private BigDecimal amount;
    private String reason;
    private LocalDateTime entryTime;
    private String recordedByName;

    public static CashEntryResponse fromEntity(CashEntry entry) {
        return CashEntryResponse.builder()
                .id(entry.getId())
                .shiftId(entry.getShift() != null ? entry.getShift().getId() : null)
                .entryType(entry.getEntryType())
                .amount(entry.getAmount())
                .reason(entry.getReason())
                .entryTime(entry.getEntryTime())
                .recordedByName(entry.getRecordedBy() != null ? entry.getRecordedBy().getFullName() : null)
                .build();
    }
}
