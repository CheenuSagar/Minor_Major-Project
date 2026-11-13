package com.pos.inventory.dto.product;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "Cost price must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Cost price format is invalid")
    private BigDecimal costPrice;

    @Min(value = 0, message = "Initial stock quantity must be non-negative")
    @Builder.Default
    private Integer stockQuantity = 0;

    @Min(value = 0, message = "Reorder threshold must be non-negative")
    @Builder.Default
    private Integer reorderThreshold = 10;

    @DecimalMin(value = "0.00", message = "Tax rate must be non-negative")
    @DecimalMax(value = "100.00", message = "Tax rate cannot exceed 100%")
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.valueOf(18.00);

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Builder.Default
    private Boolean active = true;
}
