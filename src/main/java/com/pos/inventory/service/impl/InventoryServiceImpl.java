package com.pos.inventory.service.impl;

import com.pos.inventory.dto.product.LowStockProductDto;
import com.pos.inventory.dto.product.StockAdjustmentRequest;
import com.pos.inventory.dto.product.StockInRequest;
import com.pos.inventory.entity.Product;
import com.pos.inventory.entity.StockMovement;
import com.pos.inventory.entity.StockMovementType;
import com.pos.inventory.entity.User;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.exception.ResourceNotFoundException;
import com.pos.inventory.repository.ProductRepository;
import com.pos.inventory.repository.StockMovementRepository;
import com.pos.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional
    public Product stockIn(StockInRequest request, User performedBy) {
        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Stock-in quantity must be greater than zero");
        }

        Product product = productRepository.findBySku(request.getSku().trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + request.getSku()));

        int previousStock = product.getStockQuantity();
        int newStock = previousStock + request.getQuantity();
        product.setStockQuantity(newStock);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .movementType(StockMovementType.STOCK_IN)
                .quantity(request.getQuantity())
                .previousStock(previousStock)
                .newStock(newStock)
                .reason(request.getReason() != null ? request.getReason() : "Stock replenished")
                .referenceId(request.getReferenceId())
                .performedBy(performedBy)
                .build();

        stockMovementRepository.save(movement);
        Product saved = productRepository.save(product);
        log.info("Stock-in performed for SKU {}: previous={}, added={}, new={}",
                product.getSku(), previousStock, request.getQuantity(), newStock);
        return saved;
    }

    @Override
    @Transactional
    public Product adjustStock(StockAdjustmentRequest request, User performedBy) {
        if (request.getNewQuantity() < 0) {
            throw new BadRequestException("Stock quantity cannot be negative");
        }

        Product product = productRepository.findBySku(request.getSku().trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + request.getSku()));

        int previousStock = product.getStockQuantity();
        int newStock = request.getNewQuantity();
        int diff = newStock - previousStock;
        product.setStockQuantity(newStock);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .movementType(StockMovementType.ADJUSTMENT)
                .quantity(diff)
                .previousStock(previousStock)
                .newStock(newStock)
                .reason(request.getReason())
                .referenceId("ADJUST-" + System.currentTimeMillis())
                .performedBy(performedBy)
                .build();

        stockMovementRepository.save(movement);
        Product saved = productRepository.save(product);
        log.info("Stock adjusted for SKU {}: from {} to {} (diff: {})",
                product.getSku(), previousStock, newStock, diff);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LowStockProductDto> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(LowStockProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovement> getStockMovementsByProduct(Long productId, Pageable pageable) {
        return stockMovementRepository.findByProductId(productId, pageable);
    }
}
