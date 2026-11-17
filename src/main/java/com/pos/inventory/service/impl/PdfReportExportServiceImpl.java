package com.pos.inventory.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pos.inventory.dto.cash.CashShiftResponse;
import com.pos.inventory.dto.product.ProductResponse;
import com.pos.inventory.dto.report.CashReportDto;
import com.pos.inventory.dto.report.InventoryReportDto;
import com.pos.inventory.dto.report.SalesReportDto;
import com.pos.inventory.dto.sale.SaleResponse;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.service.PdfReportExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PdfReportExportServiceImpl implements PdfReportExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    @Override
    public byte[] exportSalesReportToPdf(SalesReportDto report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.DARK_GRAY);
            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

            Paragraph title = new Paragraph("SALES & REVENUE REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph meta = new Paragraph("Total Sales: " + report.getTotalSalesCount() +
                    " | Total Revenue: ₹" + String.format("%.2f", report.getTotalNetRevenue()) +
                    " | Total Tax: ₹" + String.format("%.2f", report.getTotalTaxCollected()), subFont);
            meta.setAlignment(Element.ALIGN_CENTER);
            meta.setSpacingAfter(15f);
            document.add(meta);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.2f, 2.0f, 2.2f, 1.2f, 1.2f, 1.5f, 1.2f});
            table.setHeaderRows(1);

            String[] headers = {"Invoice Number", "Date", "Customer", "Subtotal (₹)", "Tax (₹)", "Total (₹)", "Status"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(24, 43, 73));
                cell.setPadding(5f);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            boolean alt = false;
            Color altColor = new Color(245, 247, 250);
            if (report.getSales() != null) {
                for (SaleResponse s : report.getSales()) {
                    Color bg = alt ? altColor : Color.WHITE;
                    alt = !alt;
                    addCell(table, s.getInvoiceNumber(), cellFont, Element.ALIGN_CENTER, bg);
                    addCell(table, s.getSaleDate() != null ? s.getSaleDate().format(DATE_FORMATTER) : "", cellFont, Element.ALIGN_CENTER, bg);
                    addCell(table, s.getCustomerName() != null ? s.getCustomerName() : "Walk-in", cellFont, Element.ALIGN_LEFT, bg);
                    addCell(table, String.format("%.2f", s.getSubtotal()), cellFont, Element.ALIGN_RIGHT, bg);
                    addCell(table, String.format("%.2f", s.getTaxAmount()), cellFont, Element.ALIGN_RIGHT, bg);
                    addCell(table, String.format("%.2f", s.getTotalAmount()), cellFont, Element.ALIGN_RIGHT, bg);
                    addCell(table, s.getPaymentStatus().name(), cellFont, Element.ALIGN_CENTER, bg);
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export sales PDF report", e);
            throw new BadRequestException("PDF export failed: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportInventoryReportToPdf(InventoryReportDto report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 30, 30, 30, 30);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.DARK_GRAY);
            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

            Paragraph title = new Paragraph("INVENTORY VALUATION & STOCK REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph meta = new Paragraph("Total Products: " + report.getTotalProductCount() +
                    " | Low Stock Items: " + report.getLowStockCount() +
                    " | Retail Value: ₹" + String.format("%.2f", report.getTotalInventoryValueAtRetail()), subFont);
            meta.setAlignment(Element.ALIGN_CENTER);
            meta.setSpacingAfter(15f);
            document.add(meta);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.8f, 3.2f, 2.0f, 1.2f, 1.0f, 1.2f});
            table.setHeaderRows(1);

            String[] headers = {"SKU", "Product Name", "Category", "Price (₹)", "Stock", "Status"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(24, 43, 73));
                cell.setPadding(5f);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            boolean alt = false;
            Color altColor = new Color(245, 247, 250);
            if (report.getProducts() != null) {
                for (ProductResponse p : report.getProducts()) {
                    Color bg = alt ? altColor : Color.WHITE;
                    alt = !alt;
                    addCell(table, p.getSku(), cellFont, Element.ALIGN_CENTER, bg);
                    addCell(table, p.getName(), cellFont, Element.ALIGN_LEFT, bg);
                    addCell(table, p.getCategoryName() != null ? p.getCategoryName() : "N/A", cellFont, Element.ALIGN_LEFT, bg);
                    addCell(table, String.format("%.2f", p.getPrice()), cellFont, Element.ALIGN_RIGHT, bg);
                    addCell(table, String.valueOf(p.getStockQuantity()), cellFont, Element.ALIGN_CENTER, bg);
                    addCell(table, p.isLowStock() ? "LOW STOCK" : "NORMAL", cellFont, Element.ALIGN_CENTER, bg);
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export inventory PDF report", e);
            throw new BadRequestException("PDF export failed: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportCashReportToPdf(CashReportDto report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.DARK_GRAY);
            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

            Paragraph title = new Paragraph("CASH DRAWER RECONCILIATION REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph meta = new Paragraph("Total Cash Sales: ₹" + String.format("%.2f", report.getTotalCashSales()) +
                    " | Net Cash In: ₹" + String.format("%.2f", report.getTotalCashIn()) +
                    " | Net Cash Out: ₹" + String.format("%.2f", report.getTotalCashOut()), subFont);
            meta.setAlignment(Element.ALIGN_CENTER);
            meta.setSpacingAfter(15f);
            document.add(meta);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 2.0f, 1.8f, 1.5f, 1.5f, 1.5f, 1.2f});
            table.setHeaderRows(1);

            String[] headers = {"Shift Number", "Cashier", "Opened", "Opening (₹)", "Closing (₹)", "Expected (₹)", "Status"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(24, 43, 73));
                cell.setPadding(5f);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            boolean alt = false;
            Color altColor = new Color(245, 247, 250);
            if (report.getShifts() != null) {
                for (CashShiftResponse s : report.getShifts()) {
                    Color bg = alt ? altColor : Color.WHITE;
                    alt = !alt;
                    addCell(table, s.getShiftNumber(), cellFont, Element.ALIGN_CENTER, bg);
                    addCell(table, s.getCashierName() != null ? s.getCashierName() : "", cellFont, Element.ALIGN_LEFT, bg);
                    addCell(table, s.getOpenedAt() != null ? s.getOpenedAt().format(DATE_FORMATTER) : "", cellFont, Element.ALIGN_CENTER, bg);
                    addCell(table, String.format("%.2f", s.getOpeningBalance()), cellFont, Element.ALIGN_RIGHT, bg);
                    addCell(table, s.getClosingBalance() != null ? String.format("%.2f", s.getClosingBalance()) : "-", cellFont, Element.ALIGN_RIGHT, bg);
                    addCell(table, s.getExpectedBalance() != null ? String.format("%.2f", s.getExpectedBalance()) : "-", cellFont, Element.ALIGN_RIGHT, bg);
                    addCell(table, s.getStatus().name(), cellFont, Element.ALIGN_CENTER, bg);
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export cash PDF report", e);
            throw new BadRequestException("PDF export failed: " + e.getMessage());
        }
    }

    private void addCell(PdfPTable table, String text, Font font, int alignment, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4f);
        cell.setBackgroundColor(bg);
        table.addCell(cell);
    }
}
