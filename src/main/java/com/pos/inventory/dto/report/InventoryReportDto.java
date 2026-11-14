package com.pos.inventory.dto.report;

import com.pos.inventory.dto.product.ProductResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReportDto {

    private long totalProductCount;
    private long lowStockCount;
    private BigDecimal totalInventoryValueAtCost;
    private BigDecimal totalInventoryValueAtRetail;
    private List<ProductResponse> products;
}
