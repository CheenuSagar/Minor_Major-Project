package com.pos.inventory.repository;

import com.pos.inventory.entity.CashShift;
import com.pos.inventory.entity.ShiftStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashShiftRepository extends JpaRepository<CashShift, Long> {

    Optional<CashShift> findByCashierIdAndStatus(Long cashierId, ShiftStatus status);

    Optional<CashShift> findTopByCashierIdOrderByOpenedAtDesc(Long cashierId);

    @Query("SELECT cs FROM CashShift cs WHERE " +
           "(:fromDate IS NULL OR cs.openedAt >= :fromDate) AND " +
           "(:toDate IS NULL OR cs.openedAt <= :toDate) AND " +
           "(:cashierId IS NULL OR cs.cashier.id = :cashierId) AND " +
           "(:status IS NULL OR cs.status = :status)")
    Page<CashShift> findShiftsWithFilters(@Param("fromDate") LocalDateTime fromDate,
                                          @Param("toDate") LocalDateTime toDate,
                                          @Param("cashierId") Long cashierId,
                                          @Param("status") ShiftStatus status,
                                          Pageable pageable);

    @Query("SELECT cs FROM CashShift cs WHERE " +
           "(:fromDate IS NULL OR cs.openedAt >= :fromDate) AND " +
           "(:toDate IS NULL OR cs.openedAt <= :toDate)")
    List<CashShift> findShiftsBetweenDates(@Param("fromDate") LocalDateTime fromDate,
                                           @Param("toDate") LocalDateTime toDate);
}
