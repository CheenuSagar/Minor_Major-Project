package com.pos.inventory.repository;

import com.pos.inventory.entity.CashEntry;
import com.pos.inventory.entity.CashEntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CashEntryRepository extends JpaRepository<CashEntry, Long> {

    List<CashEntry> findByShiftIdOrderByEntryTimeAsc(Long shiftId);

    @Query("SELECT COALESCE(SUM(ce.amount), 0) FROM CashEntry ce " +
           "WHERE ce.shift.id = :shiftId AND ce.entryType = :entryType")
    BigDecimal sumAmountByShiftIdAndEntryType(@Param("shiftId") Long shiftId,
                                              @Param("entryType") CashEntryType entryType);

    @Query("SELECT ce FROM CashEntry ce WHERE ce.entryTime BETWEEN :startDate AND :endDate ORDER BY ce.entryTime DESC")
    List<CashEntry> findBetweenDates(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}
