package com.ex.eventpro_api.reservation.internal.service;

import com.ex.eventpro_api.reservation.internal.dto.request.ReservationRequest;
import com.ex.eventpro_api.reservation.internal.dto.response.ReservationResponse;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
}
