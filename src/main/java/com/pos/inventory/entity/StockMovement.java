package com.pos.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "STOCK_MOVEMENTS", indexes = {
    @Index(name = "IDX_SM_PRODUCT", columnList = "PRODUCT_ID"),
    @Index(name = "IDX_SM_TYPE", columnList = "MOVEMENT_TYPE"),
    @Index(name = "IDX_SM_CREATED_AT", columnList = "CREATED_AT")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "MOVEMENT_TYPE", nullable = false, length = 30)
    private StockMovementType movementType;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "PREVIOUS_STOCK", nullable = false)
    private Integer previousStock;

    @Column(name = "NEW_STOCK", nullable = false)
    private Integer newStock;

    @Column(name = "REASON", length = 255)
    private String reason;

    @Column(name = "REFERENCE_ID", length = 100)
    private String referenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMED_BY")
    private User performedBy;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
