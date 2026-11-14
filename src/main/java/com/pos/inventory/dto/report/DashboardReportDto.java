package com.pos.inventory.dto.report;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardReportDto {

    private BigDecimal dailySalesTotal;
    private long dailyCompletedSalesCount;
    private BigDecimal weeklySalesTotal;
    private long weeklyCompletedSalesCount;
    private BigDecimal monthlySalesTotal;
    private long monthlyCompletedSalesCount;
    private long lowStockCount;
    private List<TopProductDto> topSellingProducts;
}
