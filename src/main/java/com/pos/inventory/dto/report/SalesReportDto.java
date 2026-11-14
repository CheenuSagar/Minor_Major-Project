package com.pos.inventory.dto.report;

import com.pos.inventory.dto.sale.SaleResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesReportDto {

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private long totalSalesCount;
    private BigDecimal totalGrossRevenue;
    private BigDecimal totalDiscountAmount;
    private BigDecimal totalTaxCollected;
    private BigDecimal totalNetRevenue;
    private List<SaleResponse> sales;
}
