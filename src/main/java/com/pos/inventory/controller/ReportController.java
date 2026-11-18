package com.pos.inventory.controller;

import com.pos.inventory.dto.common.ApiResponse;
import com.pos.inventory.dto.report.CashReportDto;
import com.pos.inventory.dto.report.DashboardReportDto;
import com.pos.inventory.dto.report.InventoryReportDto;
import com.pos.inventory.dto.report.SalesReportDto;
import com.pos.inventory.entity.PaymentMode;
import com.pos.inventory.service.ExcelExportService;
import com.pos.inventory.service.PdfReportExportService;
import com.pos.inventory.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Analytics & Reports", description = "Endpoints for business dashboard, sales, inventory, and cash reports with PDF/XLSX export")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('STORE_MANAGER')")
public class ReportController {

    private final ReportService reportService;
    private final ExcelExportService excelExportService;
    private final PdfReportExportService pdfReportExportService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get executive dashboard statistics", description = "Returns daily, weekly, monthly sales totals, top 5 selling products, and low stock alert count")
    public ResponseEntity<ApiResponse<DashboardReportDto>> getDashboardReport() {
        DashboardReportDto report = reportService.getDashboardReport();
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/sales")
    @Operation(summary = "Sales report with optional PDF or Excel export", description = "Pass format=pdf or format=xlsx for file export; defaults to JSON")
    public ResponseEntity<?> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) PaymentMode paymentMode,
            @RequestParam(required = false) String format) {

        SalesReportDto report = reportService.getSalesReport(fromDate, toDate, categoryId, paymentMode);

        if ("xlsx".equalsIgnoreCase(format)) {
            byte[] fileBytes = excelExportService.exportSalesReportToExcel(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales-report.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(fileBytes);
        } else if ("pdf".equalsIgnoreCase(format)) {
            byte[] fileBytes = pdfReportExportService.exportSalesReportToPdf(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileBytes);
        }

        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/inventory")
    @Operation(summary = "Inventory valuation & status report with PDF/Excel export")
    public ResponseEntity<?> getInventoryReport(@RequestParam(required = false) String format) {
        InventoryReportDto report = reportService.getInventoryReport();

        if ("xlsx".equalsIgnoreCase(format)) {
            byte[] fileBytes = excelExportService.exportInventoryReportToExcel(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory-report.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(fileBytes);
        } else if ("pdf".equalsIgnoreCase(format)) {
            byte[] fileBytes = pdfReportExportService.exportInventoryReportToPdf(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileBytes);
        }

        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/cash")
    @Operation(summary = "Cash drawer reconciliation report with PDF/Excel export")
    public ResponseEntity<?> getCashReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) String format) {

        CashReportDto report = reportService.getCashReport(fromDate, toDate);

        if ("xlsx".equalsIgnoreCase(format)) {
            byte[] fileBytes = excelExportService.exportCashReportToExcel(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cash-report.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(fileBytes);
        } else if ("pdf".equalsIgnoreCase(format)) {
            byte[] fileBytes = pdfReportExportService.exportCashReportToPdf(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cash-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileBytes);
        }

        return ResponseEntity.ok(ApiResponse.ok(report));
    }
}
