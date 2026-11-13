package com.ex.eventpro_api.reservation.internal.service;

import com.ex.eventpro_api.reservation.internal.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.internal.dto.response.ReservationResponse;
import com.ex.eventpro_api.reservation.internal.dto.response.TopAmountResponse;
import com.ex.eventpro_api.reservation.internal.dto.response.TopTicketsClientResponse;
import com.ex.eventpro_api.reservation.internal.entity.Reservation;

import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);

    List<ReservationResponse> getClientReservations(Long clientId);

    TopAmountResponse findTopAmountClient();

    TopTicketsClientResponse findTopTicketClient();

    /// mise en situation 1
    List<ReservationResponse> findReservationByEmailAndCateg(String email, String nomCateg, int qteMin,Long eventId);

}
