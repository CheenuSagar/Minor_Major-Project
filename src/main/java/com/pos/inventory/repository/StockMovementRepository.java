package com.pos.inventory.repository;

import com.pos.inventory.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);

    Page<StockMovement> findByProductId(Long productId, Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdAt BETWEEN :startDate AND :endDate ORDER BY sm.createdAt DESC")
    List<StockMovement> findBetweenDates(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
}
