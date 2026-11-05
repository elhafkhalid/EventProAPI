package com.ex.eventpro_api.reservation.internal.service;

import com.ex.eventpro_api.reservation.internal.dto.response.TicketResponse;
import com.ex.eventpro_api.reservation.internal.dto.response.TicketScanResponse;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;

import java.util.List;

public interface TicketService {
    // Appelé après le paiement réussi
    void generateTickets(Reservation reservation);

    // Appelé par l'agent de contrôle
    // Dans TicketService.java
    TicketScanResponse scanTicket(String ticketCode);
    List<TicketResponse> getTicketsByReservation(Long reservationId);

}