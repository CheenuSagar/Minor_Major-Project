package com.pos.inventory.repository;

import com.pos.inventory.entity.PaymentStatus;
import com.pos.inventory.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    @Query("SELECT s FROM Sale s LEFT JOIN FETCH s.items WHERE s.id = :id")
    Optional<Sale> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT s FROM Sale s LEFT JOIN FETCH s.items WHERE s.invoiceNumber = :invoiceNumber")
    Optional<Sale> findByInvoiceNumberWithItems(@Param("invoiceNumber") String invoiceNumber);

    @Query("SELECT s FROM Sale s WHERE " +
           "(:fromDate IS NULL OR s.saleDate >= :fromDate) AND " +
           "(:toDate IS NULL OR s.saleDate <= :toDate) AND " +
           "(:status IS NULL OR s.paymentStatus = :status)")
    Page<Sale> findSalesWithFilters(@Param("fromDate") LocalDateTime fromDate,
                                    @Param("toDate") LocalDateTime toDate,
                                    @Param("status") PaymentStatus status,
                                    Pageable pageable);

    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> findBetweenDates(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s " +
           "WHERE s.paymentStatus = 'PAID' AND s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalSalesBetweenDates(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Sale s " +
           "WHERE s.paymentStatus = 'PAID' AND s.saleDate BETWEEN :startDate AND :endDate")
    long countCompletedSalesBetweenDates(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
}
