package com.pos.inventory.controller;

import com.pos.inventory.dto.cash.*;
import com.pos.inventory.dto.common.ApiResponse;
import com.pos.inventory.dto.common.PageResponse;
import com.pos.inventory.entity.CashShift;
import com.pos.inventory.entity.ShiftStatus;
import com.pos.inventory.entity.User;
import com.pos.inventory.service.CashDrawerService;
import com.pos.inventory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/cash-drawer")
@RequiredArgsConstructor
@Tag(name = "Cash Drawer Management", description = "Endpoints for cashier shifts, cash drawer reconciliation, and cash-in/out floats")
@SecurityRequirement(name = "bearerAuth")
public class CashDrawerController {

    private final CashDrawerService cashDrawerService;
    private final UserService userService;

    @PostMapping("/shift/open")
    @PreAuthorize("hasAnyRole('STORE_MANAGER', 'CUSTOMER')")
    @Operation(summary = "Open a new cashier cash shift", description = "Initializes a shift with opening balance float")
    public ResponseEntity<ApiResponse<CashShiftResponse>> openShift(@Valid @RequestBody OpenShiftRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        CashShiftResponse shift = cashDrawerService.openShift(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cash shift opened successfully", shift));
    }

    @PostMapping("/shift/{id}/close")
    @PreAuthorize("hasAnyRole('STORE_MANAGER', 'CUSTOMER')")
    @Operation(summary = "Close cash shift and reconcile drawer", description = "Takes actual counted cash, computes expected balance and discrepancy")
    public ResponseEntity<ApiResponse<CashShiftResponse>> closeShift(
            @PathVariable Long id,
            @Valid @RequestBody CloseShiftRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        CashShiftResponse shift = cashDrawerService.closeShift(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Cash shift closed and reconciled", shift));
    }

    @PostMapping("/entry")
    @PreAuthorize("hasAnyRole('STORE_MANAGER', 'CUSTOMER')")
    @Operation(summary = "Add cash-in or cash-out float entry", description = "Records paid-in or paid-out petty cash movements")
    public ResponseEntity<ApiResponse<CashEntryResponse>> addCashEntry(@Valid @RequestBody CashEntryRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        CashEntryResponse entry = cashDrawerService.recordCashEntry(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cash entry recorded successfully", entry));
    }

    @GetMapping("/shift/current")
    @Operation(summary = "Get currently active shift for the authenticated cashier")
    public ResponseEntity<ApiResponse<CashShiftResponse>> getCurrentShift() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Optional<CashShift> shift = cashDrawerService.getActiveShiftForCashier(currentUser.getId());
        return shift.map(cashShift -> ResponseEntity.ok(ApiResponse.ok(CashShiftResponse.fromEntity(cashShift))))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.ok("No active shift found", null)));
    }

    @GetMapping("/shift/{id}")
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @Operation(summary = "Get shift details by ID")
    public ResponseEntity<ApiResponse<CashShiftResponse>> getShiftById(@PathVariable Long id) {
        CashShiftResponse shift = cashDrawerService.getShiftDetails(id);
        return ResponseEntity.ok(ApiResponse.ok(shift));
    }

    @GetMapping("/shifts")
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @Operation(summary = "List cash drawer shifts with filters")
    public ResponseEntity<ApiResponse<PageResponse<CashShiftResponse>>> getShifts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) Long cashierId,
            @RequestParam(required = false) ShiftStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("openedAt").descending());
        Page<CashShiftResponse> shifts = cashDrawerService.getShifts(fromDate, toDate, cashierId, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(shifts)));
    }
}
