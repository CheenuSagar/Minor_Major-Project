package com.pos.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCTS", indexes = {
    @Index(name = "IDX_PRODUCT_SKU", columnList = "SKU", unique = true),
    @Index(name = "IDX_PRODUCT_CATEGORY", columnList = "CATEGORY_ID"),
    @Index(name = "IDX_PRODUCT_ACTIVE", columnList = "ACTIVE")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    @Column(name = "SKU", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "PRICE", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "COST_PRICE", precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "STOCK_QUANTITY", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(name = "REORDER_THRESHOLD", nullable = false)
    @Builder.Default
    private Integer reorderThreshold = 10;

    @Column(name = "TAX_RATE", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.valueOf(18.00); // 18% GST standard

    @Column(name = "ACTIVE", nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
