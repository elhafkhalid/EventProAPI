package com.ex.eventpro_api.reservation.service;

import com.ex.eventpro_api.reservation.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.dto.response.ReservationResponse;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
}
