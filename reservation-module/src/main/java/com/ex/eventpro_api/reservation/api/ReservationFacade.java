package com.ex.eventpro_api.reservation.api;

import com.ex.eventpro_api.reservation.api.dto.ReservationDto;

import java.util.List;

public interface ReservationFacade {

    // Pour que le module Payment puisse vérifier le montant et le statut
    ReservationDto getReservationById(Long reservationId);

    // Pour que le module Payment puisse confirmer la réservation après paiement
    void confirmReservation(Long reservationId);

    List<ReservationCount> findClientsWithReservations();
}