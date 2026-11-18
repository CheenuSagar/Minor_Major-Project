package com.pos.inventory.controller;

import com.pos.inventory.dto.common.ApiResponse;
import com.pos.inventory.dto.common.PageResponse;
import com.pos.inventory.dto.sale.SaleRequest;
import com.pos.inventory.dto.sale.SaleResponse;
import com.pos.inventory.entity.PaymentStatus;
import com.pos.inventory.entity.Sale;
import com.pos.inventory.entity.User;
import com.pos.inventory.service.PdfReceiptService;
import com.pos.inventory.service.SaleService;
import com.pos.inventory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales Transactions", description = "Endpoints for POS billing, checkout transactions, and PDF invoice receipt generation")
@SecurityRequirement(name = "bearerAuth")
public class SaleController {

    private final SaleService saleService;
    private final PdfReceiptService pdfReceiptService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Process a new sales transaction", description = "Calculates prices, taxes, discounts, auto-decrements stock, and saves sale in a single atomic transaction")
    public ResponseEntity<ApiResponse<SaleResponse>> createSale(@Valid @RequestBody SaleRequest request) {
        User currentCashier = userService.getCurrentAuthenticatedUser();
        SaleResponse sale = saleService.createSale(request, currentCashier);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Sale transaction completed successfully", sale));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale details by ID", description = "Returns full sale invoice including itemized lines")
    public ResponseEntity<ApiResponse<SaleResponse>> getSaleById(@PathVariable Long id) {
        SaleResponse sale = saleService.getSaleById(id);
        return ResponseEntity.ok(ApiResponse.ok(sale));
    }

    @GetMapping("/invoice/{invoiceNumber}")
    @Operation(summary = "Get sale details by invoice number")
    public ResponseEntity<ApiResponse<SaleResponse>> getSaleByInvoice(@PathVariable String invoiceNumber) {
        SaleResponse sale = saleService.getSaleByInvoice(invoiceNumber);
        return ResponseEntity.ok(ApiResponse.ok(sale));
    }

    @GetMapping
    @Operation(summary = "List sales transactions with date & status filters")
    public ResponseEntity<ApiResponse<PageResponse<SaleResponse>>> getAllSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SaleResponse> sales = saleService.getAllSales(fromDate, toDate, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(sales)));
    }

    @GetMapping(value = "/{id}/receipt", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Download printable PDF sales receipt", description = "Generates a formatted PDF receipt containing bill number, date, items, tax breakdown, and total")
    public ResponseEntity<byte[]> getSaleReceiptPdf(@PathVariable Long id) {
        Sale sale = saleService.getSaleEntityById(id);
        byte[] pdfBytes = pdfReceiptService.generateReceiptPdf(sale);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "receipt-" + sale.getInvoiceNumber() + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
