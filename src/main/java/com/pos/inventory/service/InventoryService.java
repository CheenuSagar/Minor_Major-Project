package com.pos.inventory.service;

import com.pos.inventory.dto.product.LowStockProductDto;
import com.pos.inventory.dto.product.StockAdjustmentRequest;
import com.pos.inventory.dto.product.StockInRequest;
import com.pos.inventory.entity.Product;
import com.pos.inventory.entity.StockMovement;
import com.pos.inventory.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {
    Product stockIn(StockInRequest request, User performedBy);
    Product adjustStock(StockAdjustmentRequest request, User performedBy);
    List<LowStockProductDto> getLowStockProducts();
    Page<StockMovement> getStockMovementsByProduct(Long productId, Pageable pageable);
}
