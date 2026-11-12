package com.pos.inventory.repository;

import com.pos.inventory.entity.Payment;
import com.pos.inventory.entity.PaymentMode;
import com.pos.inventory.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySaleId(Long saleId);

    @Query("SELECT p FROM Payment p WHERE " +
           "(:fromDate IS NULL OR p.paymentDate >= :fromDate) AND " +
           "(:toDate IS NULL OR p.paymentDate <= :toDate) AND " +
           "(:mode IS NULL OR p.paymentMode = :mode) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Payment> findPaymentsWithFilters(@Param("fromDate") LocalDateTime fromDate,
                                         @Param("toDate") LocalDateTime toDate,
                                         @Param("mode") PaymentMode mode,
                                         @Param("status") PaymentStatus status,
                                         Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE " +
           "(:fromDate IS NULL OR p.paymentDate >= :fromDate) AND " +
           "(:toDate IS NULL OR p.paymentDate <= :toDate) AND " +
           "(:mode IS NULL OR p.paymentMode = :mode) AND " +
           "(:status IS NULL OR p.status = :status)")
    List<Payment> findPaymentsListWithFilters(@Param("fromDate") LocalDateTime fromDate,
                                             @Param("toDate") LocalDateTime toDate,
                                             @Param("mode") PaymentMode mode,
                                             @Param("status") PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.paymentMode = 'CASH' AND p.status = 'PAID' AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumCashPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
}
