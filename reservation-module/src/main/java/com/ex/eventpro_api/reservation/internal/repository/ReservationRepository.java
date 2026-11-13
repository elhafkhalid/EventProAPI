package com.ex.eventpro_api.reservation.internal.repository;

import com.ex.eventpro_api.reservation.api.ReservationCount;
import com.ex.eventpro_api.reservation.api.TopAmountClient;
import com.ex.eventpro_api.reservation.api.TopTicketsClient;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;
import com.ex.eventpro_api.reservation.internal.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatusAndExpirationDateBefore(ReservationStatus status, LocalDateTime date);

    List<Reservation> findByClientId(Long clientId);

    @Query("select r.clientId as clientId, count(r) as totalReservations from Reservation r group by r.clientId")
    List<ReservationCount> findClientsWithReservations(); // JPQL


    @Query(value = "select client_id as clientId, SUM(total_amount) as totalAmount from reservations " +
            "group by clientId order by totalAmount DESC limit 1", nativeQuery = true)
    TopAmountClient findTopAmountClient(); // natif


    @Query(value = "select r.client_id as clientId,count (t.id) as totalTickets " +
            "from tickets t " +
            "join reservation_lines rl on t.reservation_line_id = rl.id " +
            "join reservations r on rl.reservation_id = r.id " +
            "group by r.client_id " +
            "order by totalTickets DESC limit 1", nativeQuery = true
    )
    TopTicketsClient findTopTickets(); // natif



    /// mise en situation 1
    @Query(value = "select r.* from reservations r " +
            "join users u on r.client_id = u.id " +
            "join reservation_lines rl on rl.reservation_id = r.id " +
            "join ticket_categories_per_event tcpe on rl.ticket_categorie_per_event_id = tcpe.id " +
            "join ticket_categories tc on tcpe.category_id = tc.id " +
            "WHERE r.status = 'CONFIRMED' " +
            "AND u.email = :email " +
            "AND tc.name = :nomCategory " +
            "AND rl.quantity >= :qteMin " +
            "AND tcpe.event_id = :eventId ", nativeQuery = true)

    List<Reservation> findReservationByEmailAndCateg(
            @Param("email") String email,
            @Param("nomCateg") String nomCateg,
            @Param("qteMin") int qteMin,
            @Param("eventId") Long eventId
    );


}
