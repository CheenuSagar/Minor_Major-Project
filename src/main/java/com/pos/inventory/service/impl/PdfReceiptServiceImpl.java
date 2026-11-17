package com.pos.inventory.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pos.inventory.entity.Sale;
import com.pos.inventory.entity.SaleItem;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.service.PdfReceiptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PdfReceiptServiceImpl implements PdfReceiptService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    @Override
    public byte[] generateReceiptPdf(Sale sale) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font boldBodyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(16, 85, 154));

            // Store Header
            Paragraph title = new Paragraph("POINT OF SALE & INVENTORY SYSTEM", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph address = new Paragraph("Retail Store & Management Services | GSTIN: 27AABCU9603R1ZX\nEmail: contact@posproject.edu | Tel: +91 98765 43210", subtitleFont);
            address.setAlignment(Element.ALIGN_CENTER);
            document.add(address);

            Paragraph separator = new Paragraph("-------------------------------------------------------------------------------------------------------------------------", subtitleFont);
            separator.setAlignment(Element.ALIGN_CENTER);
            document.add(separator);

            // Receipt Metadata Section
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            metaTable.setSpacingBefore(10f);
            metaTable.setSpacingAfter(15f);

            PdfPCell cell1 = new PdfPCell(new Phrase("Bill / Invoice No: " + sale.getInvoiceNumber() +
                    "\nDate & Time: " + sale.getSaleDate().format(DATE_FORMATTER) +
                    "\nPayment Status: " + sale.getPaymentStatus(), bodyFont));
            cell1.setBorder(Rectangle.NO_BORDER);

            String customerName = (sale.getCustomer() != null) ? sale.getCustomer().getFullName() : "Walk-in Customer";
            String cashierName = (sale.getCashier() != null) ? sale.getCashier().getFullName() : "Store Staff";

            PdfPCell cell2 = new PdfPCell(new Phrase("Customer: " + customerName +
                    "\nCashier: " + cashierName +
                    "\nCurrency: INR (₹)", bodyFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);

            metaTable.addCell(cell1);
            metaTable.addCell(cell2);
            document.add(metaTable);

            // Items Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.2f, 3.5f, 1.2f, 0.8f, 1.0f, 1.5f});
            table.setHeaderRows(1);

            // Header Cells
            String[] headers = {"SKU", "Item Description", "Price (₹)", "Qty", "Tax (₹)", "Total (₹)"};
            Color headerBg = new Color(30, 41, 59);
            for (String h : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(h, headerFont));
                headerCell.setBackgroundColor(headerBg);
                headerCell.setPadding(6f);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(headerCell);
            }

            // Line Items
            Color alternateBg = new Color(248, 250, 252);
            boolean alt = false;
            for (SaleItem item : sale.getItems()) {
                Color rowBg = alt ? alternateBg : Color.WHITE;
                alt = !alt;

                addCell(table, item.getProductSku(), bodyFont, Element.ALIGN_CENTER, rowBg);
                addCell(table, item.getProductName(), bodyFont, Element.ALIGN_LEFT, rowBg);
                addCell(table, String.format("%.2f", item.getUnitPrice()), bodyFont, Element.ALIGN_RIGHT, rowBg);
                addCell(table, String.valueOf(item.getQuantity()), bodyFont, Element.ALIGN_CENTER, rowBg);
                addCell(table, String.format("%.2f", item.getTaxAmount()), bodyFont, Element.ALIGN_RIGHT, rowBg);
                addCell(table, String.format("%.2f", item.getTotal()), bodyFont, Element.ALIGN_RIGHT, rowBg);
            }

            document.add(table);

            // Summary Totals Table
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(45);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setSpacingBefore(15f);

            addTotalRow(totalTable, "Subtotal:", String.format("₹ %.2f", sale.getSubtotal()), bodyFont);
            if (sale.getDiscountAmount() != null && sale.getDiscountAmount().signum() > 0) {
                addTotalRow(totalTable, "Discount:", String.format("- ₹ %.2f", sale.getDiscountAmount()), bodyFont);
            }
            addTotalRow(totalTable, "Tax (GST):", String.format("₹ %.2f", sale.getTaxAmount()), bodyFont);
            addTotalRow(totalTable, "GRAND TOTAL:", String.format("₹ %.2f", sale.getTotalAmount()), totalFont);

            document.add(totalTable);

            // Footer note
            Paragraph footer = new Paragraph("\n\nThank you for shopping with us!\nItems once sold can be exchanged within 7 days with this original receipt.\nComputer Generated Invoice - No signature required.", subtitleFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF receipt for sale id: {}", sale.getId(), e);
            throw new BadRequestException("Could not generate receipt PDF: " + e.getMessage());
        }
    }

    private void addCell(PdfPTable table, String text, Font font, int alignment, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5f);
        cell.setBackgroundColor(bg);
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        labelCell.setPadding(4f);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(4f);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}
