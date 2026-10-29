package com.ex.eventpro_api.reservation.repository;

import com.ex.eventpro_api.reservation.entity.ReservationLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationLineRepository extends JpaRepository<ReservationLine, Long> {

    @Query("SELECT COALESCE(SUM(rl.quantity), 0) FROM ReservationLine rl " +
            "WHERE rl.reservation.client.id = :clientId " +
            "AND rl.category.id = :categoryId " +
            "AND rl.reservation.status IN ('PENDING_PAYMENT', 'CONFIRMED')")

    Integer countTotalQuantityBoughtByClientForCategory(
            @Param("clientId") Long clientId,
            @Param("categoryId") Long categoryId
    );
}