package com.ex.eventpro_api.reservation.internal.service;

import com.ex.eventpro_api.reservation.internal.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.internal.dto.response.ReservationResponse;

import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
    List<ReservationResponse> getClientReservations(Long clientId);
}
