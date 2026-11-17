package com.pos.inventory.service;

import com.pos.inventory.dto.cash.*;
import com.pos.inventory.entity.CashShift;
import com.pos.inventory.entity.ShiftStatus;
import com.pos.inventory.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface CashDrawerService {
    CashShiftResponse openShift(OpenShiftRequest request, User cashier);
    CashShiftResponse closeShift(Long shiftId, CloseShiftRequest request, User cashier);
    CashEntryResponse recordCashEntry(CashEntryRequest request, User performedBy);
    void recordSaleCashCollection(CashShift shift, BigDecimal amount, String invoiceNumber, User cashier);
    Optional<CashShift> getActiveShiftForCashier(Long cashierId);
    CashShiftResponse getShiftDetails(Long shiftId);
    Page<CashShiftResponse> getShifts(LocalDateTime fromDate, LocalDateTime toDate, Long cashierId, ShiftStatus status, Pageable pageable);
}
