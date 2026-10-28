package com.ex.eventpro_api.repository;

import com.ex.eventpro_api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdempotencyKey(String idempotenceKey);

    Optional<Payment> findByReservationId(Long reservationId);
}
