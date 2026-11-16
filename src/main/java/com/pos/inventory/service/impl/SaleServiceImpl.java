package com.pos.inventory.service.impl;

import com.pos.inventory.dto.sale.SaleItemRequest;
import com.pos.inventory.dto.sale.SaleRequest;
import com.pos.inventory.dto.sale.SaleResponse;
import com.pos.inventory.entity.*;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.exception.InsufficientStockException;
import com.pos.inventory.exception.ResourceNotFoundException;
import com.pos.inventory.repository.ProductRepository;
import com.pos.inventory.repository.SaleRepository;
import com.pos.inventory.repository.StockMovementRepository;
import com.pos.inventory.repository.UserRepository;
import com.pos.inventory.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public SaleResponse createSale(SaleRequest request, User cashier) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Sale must have at least one line item");
        }

        User customer = null;
        if (request.getCustomerId() != null) {
            customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));
        }

        String invoiceNumber = generateInvoiceNumber();
        LocalDateTime now = LocalDateTime.now();

        Sale sale = Sale.builder()
                .invoiceNumber(invoiceNumber)
                .saleDate(now)
                .customer(customer)
                .cashier(cashier)
                .subtotal(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .discountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .paymentStatus(PaymentStatus.PENDING)
                .notes(request.getNotes())
                .items(new ArrayList<>())
                .build();

        BigDecimal runningSubtotal = BigDecimal.ZERO;
        BigDecimal runningTax = BigDecimal.ZERO;

        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findBySku(itemReq.getSku().trim().toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + itemReq.getSku()));

            if (!product.isActive()) {
                throw new BadRequestException("Product with SKU '" + product.getSku() + "' is deactivated and cannot be sold");
            }

            int requestedQty = itemReq.getQuantity();
            if (requestedQty <= 0) {
                throw new BadRequestException("Quantity for SKU '" + product.getSku() + "' must be positive");
            }

            if (product.getStockQuantity() < requestedQty) {
                throw new InsufficientStockException("Insufficient stock for SKU '" + product.getSku() + "'. Available: "
                        + product.getStockQuantity() + ", Requested: " + requestedQty);
            }

            // Decrement product stock
            int prevStock = product.getStockQuantity();
            int newStock = prevStock - requestedQty;
            product.setStockQuantity(newStock);
            productRepository.save(product);

            // Calculate item amounts
            BigDecimal unitPrice = product.getPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(requestedQty)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal taxRate = product.getTaxRate() != null ? product.getTaxRate() : BigDecimal.ZERO;
            BigDecimal itemTax = itemSubtotal.multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal itemTotal = itemSubtotal.add(itemTax);

            runningSubtotal = runningSubtotal.add(itemSubtotal);
            runningTax = runningTax.add(itemTax);

            // Create and link line item
            SaleItem saleItem = SaleItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .unitPrice(unitPrice)
                    .quantity(requestedQty)
                    .subtotal(itemSubtotal)
                    .taxRate(taxRate)
                    .taxAmount(itemTax)
                    .total(itemTotal)
                    .build();

            sale.addItem(saleItem);

            // Record stock movement
            StockMovement movement = StockMovement.builder()
                    .product(product)
                    .movementType(StockMovementType.SALE_DEDUCTION)
                    .quantity(requestedQty)
                    .previousStock(prevStock)
                    .newStock(newStock)
                    .reason("Sold on invoice " + invoiceNumber)
                    .referenceId(invoiceNumber)
                    .performedBy(cashier)
                    .build();

            stockMovementRepository.save(movement);
        }

        sale.setSubtotal(runningSubtotal);
        sale.setTaxAmount(runningTax);

        BigDecimal discount = sale.getDiscountAmount();
        BigDecimal grandTotal = runningSubtotal.add(runningTax).subtract(discount);
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            grandTotal = BigDecimal.ZERO;
        }
        sale.setTotalAmount(grandTotal.setScale(2, RoundingMode.HALF_UP));

        Sale saved = saleRepository.save(sale);
        log.info("Successfully created sale invoice {} with {} items, total: ₹{}",
                saved.getInvoiceNumber(), saved.getItems().size(), saved.getTotalAmount());

        return SaleResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
        return SaleResponse.fromEntity(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleByInvoice(String invoiceNumber) {
        Sale sale = saleRepository.findByInvoiceNumberWithItems(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with invoice number: " + invoiceNumber));
        return SaleResponse.fromEntity(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponse> getAllSales(LocalDateTime fromDate, LocalDateTime toDate, PaymentStatus status, Pageable pageable) {
        return saleRepository.findSalesWithFilters(fromDate, toDate, status, pageable)
                .map(SaleResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Sale getSaleEntityById(Long id) {
        return saleRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
    }

    private synchronized String generateInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "INV-" + timestamp + "-" + randomSuffix;
    }
}
