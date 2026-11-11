package com.pos.inventory.repository;

import com.pos.inventory.entity.SaleItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySaleId(Long saleId);

    public interface TopProductProjection {
        Long getProductId();
        String getProductName();
        String getProductSku();
        Long getTotalQuantitySold();
        Double getTotalRevenue();
    }

    @Query("SELECT si.product.id AS productId, si.productName AS productName, si.productSku AS productSku, " +
           "SUM(si.quantity) AS totalQuantitySold, SUM(si.total) AS totalRevenue " +
           "FROM SaleItem si " +
           "WHERE si.sale.paymentStatus = 'PAID' AND si.sale.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY si.product.id, si.productName, si.productSku " +
           "ORDER BY SUM(si.quantity) DESC")
    List<TopProductProjection> findTopSellingProducts(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate,
                                                     Pageable pageable);
}
