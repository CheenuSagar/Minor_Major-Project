package com.pos.inventory.controller;

import com.pos.inventory.dto.common.ApiResponse;
import com.pos.inventory.dto.common.PageResponse;
import com.pos.inventory.dto.product.LowStockProductDto;
import com.pos.inventory.dto.product.ProductResponse;
import com.pos.inventory.dto.product.StockAdjustmentRequest;
import com.pos.inventory.dto.product.StockInRequest;
import com.pos.inventory.entity.Product;
import com.pos.inventory.entity.StockMovement;
import com.pos.inventory.entity.User;
import com.pos.inventory.service.InventoryService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory & Stock Control", description = "Endpoints for stock-in, stock adjustment, low-stock alerts, and movement audits")
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final InventoryService inventoryService;
    private final UserService userService;

    @PostMapping("/stock-in")
    @PreAuthorize("hasAnyRole('STORE_MANAGER', 'OWNER')")
    @Operation(summary = "Replenish stock (Stock-In)", description = "Adds inventory to an existing product SKU and creates an audit movement record")
    public ResponseEntity<ApiResponse<ProductResponse>> stockIn(@Valid @RequestBody StockInRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Product product = inventoryService.stockIn(request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Stock added successfully", ProductResponse.fromEntity(product)));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('STORE_MANAGER', 'OWNER')")
    @Operation(summary = "Adjust inventory stock", description = "Directly sets stock quantity (>=0) with mandatory reason for shrinkage/damage")
    public ResponseEntity<ApiResponse<ProductResponse>> adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Product product = inventoryService.adjustStock(request, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Stock adjusted successfully", ProductResponse.fromEntity(product)));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('STORE_MANAGER', 'OWNER')")
    @Operation(summary = "Low stock alert", description = "Retrieves products where quantity <= reorder threshold")
    public ResponseEntity<ApiResponse<List<LowStockProductDto>>> getLowStockProducts() {
        List<LowStockProductDto> lowStock = inventoryService.getLowStockProducts();
        return ResponseEntity.ok(ApiResponse.ok(lowStock));
    }

    @GetMapping("/movements/{productId}")
    @PreAuthorize("hasAnyRole('STORE_MANAGER', 'OWNER')")
    @Operation(summary = "Stock movement audit trail for a product", description = "Shows all stock additions, adjustments, and sale deductions")
    public ResponseEntity<ApiResponse<PageResponse<StockMovement>>> getStockMovements(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<StockMovement> movements = inventoryService.getStockMovementsByProduct(productId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(movements)));
    }
}
