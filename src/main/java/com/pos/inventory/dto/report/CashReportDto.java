package com.pos.inventory.dto.report;

import com.pos.inventory.dto.cash.CashShiftResponse;
import com.pos.inventory.dto.payment.PaymentResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashReportDto {

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private BigDecimal totalCashSales;
    private BigDecimal totalCashIn;
    private BigDecimal totalCashOut;
    private BigDecimal netCashFlow;
    private List<CashShiftResponse> shifts;
    private List<PaymentResponse> cashPayments;
}
