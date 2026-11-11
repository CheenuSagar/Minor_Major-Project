package com.pos.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CASH_ENTRIES", indexes = {
    @Index(name = "IDX_ENTRY_SHIFT", columnList = "SHIFT_ID"),
    @Index(name = "IDX_ENTRY_TYPE", columnList = "ENTRY_TYPE"),
    @Index(name = "IDX_ENTRY_TIME", columnList = "ENTRY_TIME")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIFT_ID", nullable = false)
    private CashShift shift;

    @Enumerated(EnumType.STRING)
    @Column(name = "ENTRY_TYPE", nullable = false, length = 30)
    private CashEntryType entryType;

    @Column(name = "AMOUNT", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "REASON", length = 255)
    private String reason;

    @Column(name = "ENTRY_TIME", nullable = false)
    private LocalDateTime entryTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECORDED_BY")
    private User recordedBy;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
