package com.pos.inventory.service;

import com.pos.inventory.dto.report.CashReportDto;
import com.pos.inventory.dto.report.InventoryReportDto;
import com.pos.inventory.dto.report.SalesReportDto;

public interface ExcelExportService {
    byte[] exportSalesReportToExcel(SalesReportDto report);
    byte[] exportInventoryReportToExcel(InventoryReportDto report);
    byte[] exportCashReportToExcel(CashReportDto report);
}
