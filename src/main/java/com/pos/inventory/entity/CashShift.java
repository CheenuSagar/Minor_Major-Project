package com.pos.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CASH_SHIFTS", indexes = {
    @Index(name = "IDX_SHIFT_NUMBER", columnList = "SHIFT_NUMBER", unique = true),
    @Index(name = "IDX_SHIFT_CASHIER", columnList = "CASHIER_ID"),
    @Index(name = "IDX_SHIFT_STATUS", columnList = "STATUS")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SHIFT_NUMBER", nullable = false, unique = true, length = 50)
    private String shiftNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CASHIER_ID", nullable = false)
    private User cashier;

    @Column(name = "OPENED_AT", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "CLOSED_AT")
    private LocalDateTime closedAt;

    @Column(name = "OPENING_BALANCE", nullable = false, precision = 12, scale = 2)
    private BigDecimal openingBalance;

    @Column(name = "CLOSING_BALANCE", precision = 12, scale = 2)
    private BigDecimal closingBalance;

    @Column(name = "EXPECTED_BALANCE", precision = 12, scale = 2)
    private BigDecimal expectedBalance;

    @Column(name = "DIFFERENCE", precision = 12, scale = 2)
    private BigDecimal difference;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    @Builder.Default
    private ShiftStatus status = ShiftStatus.OPEN;

    @Column(name = "NOTES", length = 255)
    private String notes;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CashEntry> entries = new ArrayList<>();

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
