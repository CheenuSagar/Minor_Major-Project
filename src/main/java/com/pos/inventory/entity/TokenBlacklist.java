package com.pos.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TOKEN_BLACKLIST", indexes = {
    @Index(name = "IDX_BLACKLIST_TOKEN", columnList = "TOKEN")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TOKEN", nullable = false, length = 512, unique = true)
    private String token;

    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "REVOKED_AT", nullable = false)
    private LocalDateTime revokedAt;
}
