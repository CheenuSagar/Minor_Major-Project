package com.pos.inventory.service;

import com.pos.inventory.dto.report.CashReportDto;
import com.pos.inventory.dto.report.DashboardReportDto;
import com.pos.inventory.dto.report.InventoryReportDto;
import com.pos.inventory.dto.report.SalesReportDto;
import com.pos.inventory.entity.PaymentMode;

import java.time.LocalDateTime;

public interface ReportService {
    DashboardReportDto getDashboardReport();
    SalesReportDto getSalesReport(LocalDateTime fromDate, LocalDateTime toDate, Long categoryId, PaymentMode paymentMode);
    InventoryReportDto getInventoryReport();
    CashReportDto getCashReport(LocalDateTime fromDate, LocalDateTime toDate);
}
