package com.pos.inventory.dto.report;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopProductDto {

    private Long productId;
    private String productName;
    private String productSku;
    private Long quantitySold;
    private BigDecimal revenue;
}
