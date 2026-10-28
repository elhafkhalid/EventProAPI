package com.ex.eventpro_api.repository;

import com.ex.eventpro_api.entity.Reservation;
import com.ex.eventpro_api.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReference(String reference);

    List<Reservation> findByStatusAndExpirationDateBefore(ReservationStatus status, LocalDateTime date);
}
