package com.pos.inventory.service;

import com.pos.inventory.dto.report.CashReportDto;
import com.pos.inventory.dto.report.InventoryReportDto;
import com.pos.inventory.dto.report.SalesReportDto;

public interface PdfReportExportService {
    byte[] exportSalesReportToPdf(SalesReportDto report);
    byte[] exportInventoryReportToPdf(InventoryReportDto report);
    byte[] exportCashReportToPdf(CashReportDto report);
}
