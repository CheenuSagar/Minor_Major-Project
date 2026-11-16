package com.pos.inventory.service;

import com.pos.inventory.dto.product.ProductRequest;
import com.pos.inventory.dto.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    ProductResponse getProductById(Long id);
    ProductResponse getProductBySku(String sku);
    void deleteProduct(Long id); // soft deactivate
    Page<ProductResponse> getAllProducts(String keyword, Long categoryId, Boolean active, Pageable pageable);
    Page<ProductResponse> searchPublicProducts(String keyword, Long categoryId, Pageable pageable);
}
