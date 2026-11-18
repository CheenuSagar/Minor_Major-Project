package com.pos.inventory.service.impl;

import com.pos.inventory.dto.cash.CashShiftResponse;
import com.pos.inventory.dto.payment.PaymentResponse;
import com.pos.inventory.dto.product.ProductResponse;
import com.pos.inventory.dto.report.*;
import com.pos.inventory.dto.sale.SaleResponse;
import com.pos.inventory.entity.*;
import com.pos.inventory.repository.*;
import com.pos.inventory.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final CashShiftRepository cashShiftRepository;
    private final CashEntryRepository cashEntryRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardReportDto getDashboardReport() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        LocalDateTime startOfWeek = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        BigDecimal dailySales = saleRepository.sumTotalSalesBetweenDates(startOfDay, endOfDay);
        long dailyCount = saleRepository.countCompletedSalesBetweenDates(startOfDay, endOfDay);

        BigDecimal weeklySales = saleRepository.sumTotalSalesBetweenDates(startOfWeek, endOfDay);
        long weeklyCount = saleRepository.countCompletedSalesBetweenDates(startOfWeek, endOfDay);

        BigDecimal monthlySales = saleRepository.sumTotalSalesBetweenDates(startOfMonth, endOfDay);
        long monthlyCount = saleRepository.countCompletedSalesBetweenDates(startOfMonth, endOfDay);

        long lowStockCount = productRepository.countLowStockProducts();

        // Top 5 selling products in past 30 days
        List<SaleItemRepository.TopProductProjection> topProjections =
                saleItemRepository.findTopSellingProducts(now.minusDays(30), now, PageRequest.of(0, 5));

        List<TopProductDto> topProducts = topProjections.stream()
                .map(p -> TopProductDto.builder()
                        .productId(p.getProductId())
                        .productName(p.getProductName())
                        .productSku(p.getProductSku())
                        .quantitySold(p.getTotalQuantitySold())
                        .revenue(BigDecimal.valueOf(p.getTotalRevenue() != null ? p.getTotalRevenue() : 0.0))
                        .build())
                .collect(Collectors.toList());

        return DashboardReportDto.builder()
                .dailySalesTotal(dailySales != null ? dailySales : BigDecimal.ZERO)
                .dailyCompletedSalesCount(dailyCount)
                .weeklySalesTotal(weeklySales != null ? weeklySales : BigDecimal.ZERO)
                .weeklyCompletedSalesCount(weeklyCount)
                .monthlySalesTotal(monthlySales != null ? monthlySales : BigDecimal.ZERO)
                .monthlyCompletedSalesCount(monthlyCount)
                .lowStockCount(lowStockCount)
                .topSellingProducts(topProducts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SalesReportDto getSalesReport(LocalDateTime fromDate, LocalDateTime toDate, Long categoryId, PaymentMode paymentMode) {
        LocalDateTime start = (fromDate != null) ? fromDate : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = (toDate != null) ? toDate : LocalDateTime.now();

        List<Sale> rawSales = saleRepository.findBetweenDates(start, end);

        List<Sale> filteredSales = rawSales.stream()
                .filter(s -> {
                    if (categoryId != null) {
                        boolean matchCategory = s.getItems().stream()
                                .anyMatch(i -> i.getProduct() != null && i.getProduct().getCategory() != null
                                        && i.getProduct().getCategory().getId().equals(categoryId));
                        if (!matchCategory) return false;
                    }
                    if (paymentMode != null) {
                        boolean matchPayment = s.getPayments().stream()
                                .anyMatch(p -> p.getPaymentMode() == paymentMode && p.getStatus() == PaymentStatus.PAID);
                        if (!matchPayment) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        BigDecimal grossRevenue = BigDecimal.ZERO;
        BigDecimal discountTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        BigDecimal netRevenue = BigDecimal.ZERO;

        List<SaleResponse> saleResponses = new ArrayList<>();
        for (Sale s : filteredSales) {
            saleResponses.add(SaleResponse.fromEntity(s));
            if (s.getPaymentStatus() == PaymentStatus.PAID) {
                grossRevenue = grossRevenue.add(s.getSubtotal());
                discountTotal = discountTotal.add(s.getDiscountAmount());
                taxTotal = taxTotal.add(s.getTaxAmount());
                netRevenue = netRevenue.add(s.getTotalAmount());
            }
        }

        return SalesReportDto.builder()
                .fromDate(start)
                .toDate(end)
                .totalSalesCount(filteredSales.size())
                .totalGrossRevenue(grossRevenue)
                .totalDiscountAmount(discountTotal)
                .totalTaxCollected(taxTotal)
                .totalNetRevenue(netRevenue)
                .sales(saleResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryReportDto getInventoryReport() {
        List<Product> products = productRepository.findAll();

        long lowStockCount = 0;
        BigDecimal costVal = BigDecimal.ZERO;
        BigDecimal retailVal = BigDecimal.ZERO;

        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product p : products) {
            productResponses.add(ProductResponse.fromEntity(p));
            if (p.getStockQuantity() <= p.getReorderThreshold()) {
                lowStockCount++;
            }
            if (p.getCostPrice() != null) {
                costVal = costVal.add(p.getCostPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())));
            }
            retailVal = retailVal.add(p.getPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())));
        }

        return InventoryReportDto.builder()
                .totalProductCount(products.size())
                .lowStockCount(lowStockCount)
                .totalInventoryValueAtCost(costVal)
                .totalInventoryValueAtRetail(retailVal)
                .products(productResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CashReportDto getCashReport(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime start = (fromDate != null) ? fromDate : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = (toDate != null) ? toDate : LocalDateTime.now();

        List<CashShift> shifts = cashShiftRepository.findShiftsBetweenDates(start, end);
        List<CashShiftResponse> shiftResponses = shifts.stream()
                .map(CashShiftResponse::fromEntity)
                .collect(Collectors.toList());

        List<Payment> cashPayments = paymentRepository.findPaymentsListWithFilters(start, end, PaymentMode.CASH, PaymentStatus.PAID);
        BigDecimal totalCashSales = cashPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CashEntry> cashEntries = cashEntryRepository.findBetweenDates(start, end);
        BigDecimal totalCashIn = cashEntries.stream()
                .filter(e -> e.getEntryType() == CashEntryType.CASH_IN)
                .map(CashEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCashOut = cashEntries.stream()
                .filter(e -> e.getEntryType() == CashEntryType.CASH_OUT)
                .map(CashEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netCashFlow = totalCashSales.add(totalCashIn).subtract(totalCashOut);

        return CashReportDto.builder()
                .fromDate(start)
                .toDate(end)
                .totalCashSales(totalCashSales)
                .totalCashIn(totalCashIn)
                .totalCashOut(totalCashOut)
                .netCashFlow(netCashFlow)
                .shifts(shiftResponses)
                .cashPayments(cashPayments.stream().map(PaymentResponse::fromEntity).collect(Collectors.toList()))
                .build();
    }
}
