package com.ex.eventpro_api.reservation.internal.repository;

import com.ex.eventpro_api.reservation.internal.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Pour que l'agent puisse chercher un billet par son code
    Optional<Ticket> findByCode(String code);
}