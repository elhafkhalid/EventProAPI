package com.ex.eventpro_api.reservation.internal.repository;

import com.ex.eventpro_api.reservation.internal.entity.ReservationLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationLineRepository extends JpaRepository<ReservationLine, Long> {

    @Query("SELECT COALESCE(SUM(rl.quantity), 0) FROM ReservationLine rl " +
            "WHERE rl.reservation.clientId = :clientId " + // clientId au lieu de client.id
            "AND rl.ticketCategoriesPerEventId = :ticketId " + // ticketId au lieu de category.id
            "AND rl.reservation.status IN ('PENDING_PAYMENT', 'CONFIRMED')")
    Integer countTotalQuantityBoughtByClientForCategory(
            @Param("clientId") Long clientId,
            @Param("ticketId") Long ticketId
    );
}