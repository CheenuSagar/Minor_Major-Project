package com.pos.inventory.service.impl;

import com.pos.inventory.dto.cash.*;
import com.pos.inventory.entity.*;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.exception.ResourceNotFoundException;
import com.pos.inventory.repository.CashEntryRepository;
import com.pos.inventory.repository.CashShiftRepository;
import com.pos.inventory.service.CashDrawerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashDrawerServiceImpl implements CashDrawerService {

    private final CashShiftRepository cashShiftRepository;
    private final CashEntryRepository cashEntryRepository;

    @Override
    @Transactional
    public CashShiftResponse openShift(OpenShiftRequest request, User cashier) {
        // Verify cashier doesn't already have an open shift
        Optional<CashShift> existingOpen = cashShiftRepository.findByCashierIdAndStatus(cashier.getId(), ShiftStatus.OPEN);
        if (existingOpen.isPresent()) {
            throw new BadRequestException("An open cash shift (" + existingOpen.get().getShiftNumber() + ") already exists for this cashier");
        }

        String shiftNumber = "SHIFT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"))
                + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        CashShift shift = CashShift.builder()
                .shiftNumber(shiftNumber)
                .cashier(cashier)
                .openedAt(LocalDateTime.now())
                .openingBalance(request.getOpeningBalance())
                .status(ShiftStatus.OPEN)
                .notes(request.getNotes())
                .build();

        CashShift saved = cashShiftRepository.save(shift);

        // Record opening balance entry
        CashEntry entry = CashEntry.builder()
                .shift(saved)
                .entryType(CashEntryType.CASH_IN)
                .amount(request.getOpeningBalance())
                .reason("Opening shift float balance")
                .entryTime(saved.getOpenedAt())
                .recordedBy(cashier)
                .build();
        cashEntryRepository.save(entry);

        log.info("Opened new cash shift {} with opening balance: ₹{}", saved.getShiftNumber(), saved.getOpeningBalance());
        return CashShiftResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public CashShiftResponse closeShift(Long shiftId, CloseShiftRequest request, User cashier) {
        CashShift shift = cashShiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Cash shift not found with id: " + shiftId));

        if (shift.getStatus() == ShiftStatus.CLOSED) {
            throw new BadRequestException("Cash shift is already closed");
        }

        // Calculate expected balance:
        // Expected = Opening Balance + Total Cash In + Total Sale Collections - Total Cash Out
        BigDecimal cashInTotal = cashEntryRepository.sumAmountByShiftIdAndEntryType(shiftId, CashEntryType.CASH_IN);
        BigDecimal cashSalesTotal = cashEntryRepository.sumAmountByShiftIdAndEntryType(shiftId, CashEntryType.SALE_COLLECTION);
        BigDecimal cashOutTotal = cashEntryRepository.sumAmountByShiftIdAndEntryType(shiftId, CashEntryType.CASH_OUT);

        // Note: opening balance was entered as a CASH_IN entry, so cashInTotal already includes opening balance
        BigDecimal expectedBalance = cashInTotal.add(cashSalesTotal).subtract(cashOutTotal);
        BigDecimal actualClosing = request.getClosingBalance();
        BigDecimal difference = actualClosing.subtract(expectedBalance);

        shift.setClosedAt(LocalDateTime.now());
        shift.setClosingBalance(actualClosing);
        shift.setExpectedBalance(expectedBalance);
        shift.setDifference(difference);
        shift.setStatus(ShiftStatus.CLOSED);
        if (request.getNotes() != null) {
            shift.setNotes(request.getNotes());
        }

        CashShift saved = cashShiftRepository.save(shift);
        log.info("Closed shift {}. Expected: ₹{}, Actual: ₹{}, Difference: ₹{}",
                saved.getShiftNumber(), expectedBalance, actualClosing, difference);

        return CashShiftResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public CashEntryResponse recordCashEntry(CashEntryRequest request, User performedBy) {
        CashShift shift = cashShiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Cash shift not found with id: " + request.getShiftId()));

        if (shift.getStatus() == ShiftStatus.CLOSED) {
            throw new BadRequestException("Cannot add cash entries to a closed shift");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Cash amount must be greater than zero");
        }

        CashEntry entry = CashEntry.builder()
                .shift(shift)
                .entryType(request.getEntryType())
                .amount(request.getAmount())
                .reason(request.getReason())
                .entryTime(LocalDateTime.now())
                .recordedBy(performedBy)
                .build();

        CashEntry saved = cashEntryRepository.save(entry);
        log.info("Recorded {} entry of ₹{} for shift {}", entry.getEntryType(), entry.getAmount(), shift.getShiftNumber());
        return CashEntryResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public void recordSaleCashCollection(CashShift shift, BigDecimal amount, String invoiceNumber, User cashier) {
        if (shift != null && shift.getStatus() == ShiftStatus.OPEN) {
            CashEntry entry = CashEntry.builder()
                    .shift(shift)
                    .entryType(CashEntryType.SALE_COLLECTION)
                    .amount(amount)
                    .reason("Cash collection for Sale Invoice: " + invoiceNumber)
                    .entryTime(LocalDateTime.now())
                    .recordedBy(cashier)
                    .build();
            cashEntryRepository.save(entry);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CashShift> getActiveShiftForCashier(Long cashierId) {
        return cashShiftRepository.findByCashierIdAndStatus(cashierId, ShiftStatus.OPEN);
    }

    @Override
    @Transactional(readOnly = true)
    public CashShiftResponse getShiftDetails(Long shiftId) {
        CashShift shift = cashShiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Cash shift not found with id: " + shiftId));
        return CashShiftResponse.fromEntity(shift);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CashShiftResponse> getShifts(LocalDateTime fromDate, LocalDateTime toDate, Long cashierId, ShiftStatus status, Pageable pageable) {
        return cashShiftRepository.findShiftsWithFilters(fromDate, toDate, cashierId, status, pageable)
                .map(CashShiftResponse::fromEntity);
    }
}
