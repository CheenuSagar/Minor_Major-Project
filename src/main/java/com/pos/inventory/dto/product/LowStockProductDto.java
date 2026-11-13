package com.pos.inventory.dto.product;

import com.pos.inventory.entity.Product;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowStockProductDto {

    private Long id;
    private String name;
    private String sku;
    private Integer currentStock;
    private Integer reorderThreshold;
    private BigDecimal price;
    private String categoryName;

    public static LowStockProductDto fromEntity(Product product) {
        return LowStockProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .currentStock(product.getStockQuantity())
                .reorderThreshold(product.getReorderThreshold())
                .price(product.getPrice())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }
}
