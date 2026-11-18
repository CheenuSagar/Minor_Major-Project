package com.pos.inventory.controller;

import com.pos.inventory.dto.common.ApiResponse;
import com.pos.inventory.dto.common.PageResponse;
import com.pos.inventory.dto.product.*;
import com.pos.inventory.service.CategoryService;
import com.pos.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Product & Category Management", description = "Endpoints for managing products, categories, and public catalogue")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // --- PUBLIC CATALOGUE ---
    @GetMapping("/public/products")
    @Operation(summary = "Public product catalogue", description = "Open to guests. Allows searching products by keyword (name/SKU) and filtering by category.")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getPublicProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.searchPublicProducts(keyword, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(products)));
    }

    // --- PRODUCT MANAGEMENT (STORE_MANAGER & AUTHENTICATED) ---
    @GetMapping("/products")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all products (with optional filters)")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductResponse> products = productService.getAllProducts(keyword, categoryId, active, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(products)));
    }

    @GetMapping("/products/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @GetMapping("/products/sku/{sku}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get product by SKU / Barcode")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySku(@PathVariable String sku) {
        ProductResponse product = productService.getProductBySku(sku);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new product", description = "Allowed only for STORE_MANAGER")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Product created successfully", product));
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update an existing product", description = "Allowed only for STORE_MANAGER")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", product));
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Soft deactivate a product", description = "Sets product active flag to false (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.message("Product deactivated successfully"));
    }

    // --- CATEGORIES ---
    @PostMapping("/categories")
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create product category")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Category created successfully", category));
    }

    @GetMapping("/categories")
    @Operation(summary = "List all product categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

    @GetMapping("/categories/{id}")
    @Operation(summary = "Get category details by ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.ok(category));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('STORE_MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update category")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Category updated successfully", category));
    }
}
