package com.pos.inventory.service.impl;

import com.pos.inventory.dto.product.ProductRequest;
import com.pos.inventory.dto.product.ProductResponse;
import com.pos.inventory.entity.Category;
import com.pos.inventory.entity.Product;
import com.pos.inventory.entity.StockMovement;
import com.pos.inventory.entity.StockMovementType;
import com.pos.inventory.exception.BadRequestException;
import com.pos.inventory.exception.ResourceNotFoundException;
import com.pos.inventory.repository.CategoryRepository;
import com.pos.inventory.repository.ProductRepository;
import com.pos.inventory.repository.StockMovementRepository;
import com.pos.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku().trim().toUpperCase())) {
            throw new BadRequestException("Product with SKU '" + request.getSku() + "' already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName().trim())
                .sku(request.getSku().trim().toUpperCase())
                .description(request.getDescription())
                .price(request.getPrice())
                .costPrice(request.getCostPrice() != null ? request.getCostPrice() : request.getPrice())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .reorderThreshold(request.getReorderThreshold() != null ? request.getReorderThreshold() : 10)
                .taxRate(request.getTaxRate() != null ? request.getTaxRate() : java.math.BigDecimal.valueOf(18.00))
                .category(category)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        Product saved = productRepository.save(product);

        // Record initial stock movement if quantity > 0
        if (saved.getStockQuantity() > 0) {
            StockMovement movement = StockMovement.builder()
                    .product(saved)
                    .movementType(StockMovementType.STOCK_IN)
                    .quantity(saved.getStockQuantity())
                    .previousStock(0)
                    .newStock(saved.getStockQuantity())
                    .reason("Initial stock on creation")
                    .referenceId("INIT-" + saved.getSku())
                    .build();
            stockMovementRepository.save(movement);
        }

        log.info("Created product with id: {}, SKU: {}", saved.getId(), saved.getSku());
        return ProductResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        String newSku = request.getSku().trim().toUpperCase();
        if (!product.getSku().equalsIgnoreCase(newSku) && productRepository.existsBySku(newSku)) {
            throw new BadRequestException("Product with SKU '" + newSku + "' already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        product.setName(request.getName().trim());
        product.setSku(newSku);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        if (request.getCostPrice() != null) {
            product.setCostPrice(request.getCostPrice());
        }
        if (request.getReorderThreshold() != null) {
            product.setReorderThreshold(request.getReorderThreshold());
        }
        if (request.getTaxRate() != null) {
            product.setTaxRate(request.getTaxRate());
        }
        product.setCategory(category);
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product updated = productRepository.save(product);
        return ProductResponse.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        return ProductResponse.fromEntity(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
        log.info("Soft deleted (deactivated) product with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, Boolean active, Pageable pageable) {
        String search = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return productRepository.findAllWithFilters(search, categoryId, active, pageable)
                .map(ProductResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchPublicProducts(String keyword, Long categoryId, Pageable pageable) {
        String search = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return productRepository.searchPublicProducts(search, categoryId, pageable)
                .map(ProductResponse::fromEntity);
    }
}
