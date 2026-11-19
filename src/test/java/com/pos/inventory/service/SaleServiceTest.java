package com.pos.inventory.service;

import com.pos.inventory.dto.sale.SaleItemRequest;
import com.pos.inventory.dto.sale.SaleRequest;
import com.pos.inventory.dto.sale.SaleResponse;
import com.pos.inventory.entity.*;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.exception.InsufficientStockException;
import com.pos.inventory.repository.ProductRepository;
import com.pos.inventory.repository.SaleRepository;
import com.pos.inventory.repository.StockMovementRepository;
import com.pos.inventory.repository.UserRepository;
import com.pos.inventory.service.impl.SaleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SaleServiceImpl saleService;

    private User cashier;
    private Product product;

    @BeforeEach
    void setUp() {
        cashier = User.builder()
                .id(1L)
                .username("cashier1")
                .fullName("John Cashier")
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        product = Product.builder()
                .id(10L)
                .name("Wireless Mouse")
                .sku("ELEC-MOU-01")
                .price(new BigDecimal("500.00"))
                .stockQuantity(20)
                .reorderThreshold(5)
                .taxRate(new BigDecimal("18.00")) // 18% GST
                .category(category)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully create sale, decrement stock, and calculate subtotal, tax, and total")
    void createSale_Success() {
        SaleItemRequest itemReq = SaleItemRequest.builder()
                .sku("ELEC-MOU-01")
                .quantity(2)
                .build();

        SaleRequest saleRequest = SaleRequest.builder()
                .items(List.of(itemReq))
                .discountAmount(new BigDecimal("50.00"))
                .notes("Counter sale")
                .build();

        when(productRepository.findBySku("ELEC-MOU-01")).thenReturn(Optional.of(product));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale s = invocation.getArgument(0);
            s.setId(100L);
            return s;
        });

        SaleResponse response = saleService.createSale(saleRequest, cashier);

        assertNotNull(response);
        // 2 items * 500 = 1000 subtotal
        assertEquals(new BigDecimal("1000.00"), response.getSubtotal());
        // 18% of 1000 = 180 tax
        assertEquals(new BigDecimal("180.00"), response.getTaxAmount());
        // 1000 + 180 - 50 discount = 1130 total
        assertEquals(new BigDecimal("1130.00"), response.getTotalAmount());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());

        // Verify stock was decremented from 20 to 18
        assertEquals(18, product.getStockQuantity());
        verify(productRepository, times(1)).save(product);
        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should throw InsufficientStockException and rollback when requested quantity exceeds inventory")
    void createSale_InsufficientStock_ThrowsException() {
        SaleItemRequest itemReq = SaleItemRequest.builder()
                .sku("ELEC-MOU-01")
                .quantity(50) // only 20 in stock!
                .build();

        SaleRequest saleRequest = SaleRequest.builder()
                .items(List.of(itemReq))
                .build();

        when(productRepository.findBySku("ELEC-MOU-01")).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> saleService.createSale(saleRequest, cashier));

        // Ensure stock was NOT decremented and sale was NOT saved
        assertEquals(20, product.getStockQuantity());
        verify(saleRepository, never()).save(any(Sale.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException if product is deactivated")
    void createSale_DeactivatedProduct_ThrowsException() {
        product.setActive(false);

        SaleItemRequest itemReq = SaleItemRequest.builder()
                .sku("ELEC-MOU-01")
                .quantity(1)
                .build();

        SaleRequest saleRequest = SaleRequest.builder()
                .items(List.of(itemReq))
                .build();

        when(productRepository.findBySku("ELEC-MOU-01")).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class, () -> saleService.createSale(saleRequest, cashier));
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should find sale by invoice number")
    void getSaleByInvoice_Success() {
        Sale sale = Sale.builder()
                .id(1L)
                .invoiceNumber("INV-20261118-TEST")
                .saleDate(LocalDateTime.now())
                .cashier(cashier)
                .subtotal(new BigDecimal("500.00"))
                .taxAmount(new BigDecimal("90.00"))
                .totalAmount(new BigDecimal("590.00"))
                .paymentStatus(PaymentStatus.PAID)
                .build();

        when(saleRepository.findByInvoiceNumberWithItems("INV-20261118-TEST")).thenReturn(Optional.of(sale));

        SaleResponse response = saleService.getSaleByInvoice("INV-20261118-TEST");

        assertNotNull(response);
        assertEquals("INV-20261118-TEST", response.getInvoiceNumber());
        assertEquals(PaymentStatus.PAID, response.getPaymentStatus());
    }
}
