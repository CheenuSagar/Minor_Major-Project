package com.pos.inventory.service.impl;

import com.pos.inventory.dto.cash.CashShiftResponse;
import com.pos.inventory.dto.product.ProductResponse;
import com.pos.inventory.dto.report.CashReportDto;
import com.pos.inventory.dto.report.InventoryReportDto;
import com.pos.inventory.dto.report.SalesReportDto;
import com.pos.inventory.dto.sale.SaleResponse;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.service.ExcelExportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ExcelExportServiceImpl implements ExcelExportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public byte[] exportSalesReportToExcel(SalesReportDto report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sales Report");

            // Header Style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // Title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("SALES REPORT - POS INVENTORY SYSTEM");
            titleCell.setCellStyle(headerStyle);

            // Column Headers
            Row headerRow = sheet.createRow(2);
            String[] headers = {"Invoice No", "Date", "Customer", "Cashier", "Subtotal (INR)", "Discount (INR)", "Tax (INR)", "Total (INR)", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowIdx = 3;
            if (report.getSales() != null) {
                for (SaleResponse sale : report.getSales()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(sale.getInvoiceNumber());
                    row.createCell(1).setCellValue(sale.getSaleDate() != null ? sale.getSaleDate().format(DATE_TIME_FORMATTER) : "");
                    row.createCell(2).setCellValue(sale.getCustomerName() != null ? sale.getCustomerName() : "Walk-in");
                    row.createCell(3).setCellValue(sale.getCashierName() != null ? sale.getCashierName() : "");

                    Cell subtotalCell = row.createCell(4);
                    subtotalCell.setCellValue(sale.getSubtotal().doubleValue());
                    subtotalCell.setCellStyle(currencyStyle);

                    Cell discountCell = row.createCell(5);
                    discountCell.setCellValue(sale.getDiscountAmount().doubleValue());
                    discountCell.setCellStyle(currencyStyle);

                    Cell taxCell = row.createCell(6);
                    taxCell.setCellValue(sale.getTaxAmount().doubleValue());
                    taxCell.setCellStyle(currencyStyle);

                    Cell totalCell = row.createCell(7);
                    totalCell.setCellValue(sale.getTotalAmount().doubleValue());
                    totalCell.setCellStyle(currencyStyle);

                    row.createCell(8).setCellValue(sale.getPaymentStatus().name());
                }
            }

            // Summary row
            Row sumRow = sheet.createRow(rowIdx + 1);
            Cell sumLabel = sumRow.createCell(3);
            sumLabel.setCellValue("TOTAL REVENUE:");
            sumLabel.setCellStyle(headerStyle);

            Cell sumVal = sumRow.createCell(7);
            sumVal.setCellValue(report.getTotalNetRevenue() != null ? report.getTotalNetRevenue().doubleValue() : 0.0);
            sumVal.setCellStyle(currencyStyle);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export sales to Excel", e);
            throw new BadRequestException("Excel export failed: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportInventoryReportToExcel(InventoryReportDto report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Inventory Status");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("INVENTORY STATUS REPORT");
            titleCell.setCellStyle(headerStyle);

            Row headerRow = sheet.createRow(2);
            String[] headers = {"SKU", "Product Name", "Category", "Cost Price (INR)", "Retail Price (INR)", "Stock Qty", "Reorder Level", "Low Stock Alert"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 3;
            if (report.getProducts() != null) {
                for (ProductResponse prod : report.getProducts()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(prod.getSku());
                    row.createCell(1).setCellValue(prod.getName());
                    row.createCell(2).setCellValue(prod.getCategoryName() != null ? prod.getCategoryName() : "N/A");

                    Cell costCell = row.createCell(3);
                    costCell.setCellValue(prod.getCostPrice() != null ? prod.getCostPrice().doubleValue() : 0.0);
                    costCell.setCellStyle(currencyStyle);

                    Cell priceCell = row.createCell(4);
                    priceCell.setCellValue(prod.getPrice() != null ? prod.getPrice().doubleValue() : 0.0);
                    priceCell.setCellStyle(currencyStyle);

                    row.createCell(5).setCellValue(prod.getStockQuantity());
                    row.createCell(6).setCellValue(prod.getReorderThreshold());
                    row.createCell(7).setCellValue(prod.isLowStock() ? "YES (REORDER)" : "OK");
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export inventory to Excel", e);
            throw new BadRequestException("Excel export failed: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportCashReportToExcel(CashReportDto report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Cash Drawer Report");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("CASH DRAWER & RECONCILIATION REPORT");
            titleCell.setCellStyle(headerStyle);

            Row headerRow = sheet.createRow(2);
            String[] headers = {"Shift Number", "Cashier", "Opened At", "Closed At", "Opening Bal (INR)", "Closing Bal (INR)", "Expected Bal (INR)", "Difference (INR)", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 3;
            if (report.getShifts() != null) {
                for (CashShiftResponse shift : report.getShifts()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(shift.getShiftNumber());
                    row.createCell(1).setCellValue(shift.getCashierName() != null ? shift.getCashierName() : "");
                    row.createCell(2).setCellValue(shift.getOpenedAt() != null ? shift.getOpenedAt().format(DATE_TIME_FORMATTER) : "");
                    row.createCell(3).setCellValue(shift.getClosedAt() != null ? shift.getClosedAt().format(DATE_TIME_FORMATTER) : "ACTIVE");

                    Cell openCell = row.createCell(4);
                    openCell.setCellValue(shift.getOpeningBalance().doubleValue());
                    openCell.setCellStyle(currencyStyle);

                    Cell closeCell = row.createCell(5);
                    closeCell.setCellValue(shift.getClosingBalance() != null ? shift.getClosingBalance().doubleValue() : 0.0);
                    closeCell.setCellStyle(currencyStyle);

                    Cell expCell = row.createCell(6);
                    expCell.setCellValue(shift.getExpectedBalance() != null ? shift.getExpectedBalance().doubleValue() : 0.0);
                    expCell.setCellStyle(currencyStyle);

                    Cell diffCell = row.createCell(7);
                    diffCell.setCellValue(shift.getDifference() != null ? shift.getDifference().doubleValue() : 0.0);
                    diffCell.setCellStyle(currencyStyle);

                    row.createCell(8).setCellValue(shift.getStatus().name());
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export cash report to Excel", e);
            throw new BadRequestException("Excel export failed: " + e.getMessage());
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
}
